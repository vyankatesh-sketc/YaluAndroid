package com.yalu.india.data.repository

import com.google.gson.JsonParser
import com.yalu.india.core.NetworkResult
import com.yalu.india.core.TokenManager
import com.yalu.india.network.YaluApiService
import com.yalu.india.network.models.AuthResponse
import com.yalu.india.network.models.LoginRequest
import com.yalu.india.network.models.ProfileResponse
import com.yalu.india.network.models.RegisterRequest
import com.yalu.india.network.models.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val apiService: YaluApiService,
    private val tokenManager: TokenManager
) {

    val isLoggedIn: StateFlow<Boolean> = tokenManager.isLoggedInFlow

    fun getCurrentUser(): UserDto? {
        val id = tokenManager.getUserId()
        if (id.isNullOrBlank()) return null
        return UserDto(
            id = id,
            name = tokenManager.getUserName() ?: "User",
            email = tokenManager.getUserEmail(),
            phone = tokenManager.getUserPhone()
        )
    }

    suspend fun login(loginInput: String, pass: String): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val trimmedInput = loginInput.trim()
            val request = LoginRequest(
                login = trimmedInput,
                email = trimmedInput,
                password = pass
            )
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.authToken
                if (!token.isNullOrBlank()) {
                    tokenManager.saveToken(token)
                    body.authUser?.let { u ->
                        tokenManager.saveUserData(u.id, u.displayName, u.email, u.phone)
                    }
                    NetworkResult.Success(body)
                } else if (body.success) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(body.message ?: "Login failed. Please check your credentials.")
                }
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Login failed (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: Unable to connect to Yalu servers. Check your internet connection.", throwable = e)
        } catch (e: HttpException) {
            NetworkResult.Error("Server error (${e.code()}): ${e.message()}", statusCode = e.code(), throwable = e)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unexpected error during login", throwable = e)
        }
    }

    suspend fun register(name: String, email: String, phone: String, pass: String): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                password = pass,
                passwordConfirmation = pass
            )
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.authToken
                if (!token.isNullOrBlank()) {
                    tokenManager.saveToken(token)
                    body.authUser?.let { u ->
                        tokenManager.saveUserData(u.id, u.displayName, u.email, u.phone)
                    }
                    NetworkResult.Success(body)
                } else if (body.success) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(body.message ?: "Registration failed.")
                }
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Registration failed (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: Unable to connect to Yalu servers. Check your internet connection.", throwable = e)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unexpected error during registration", throwable = e)
        }
    }

    suspend fun getProfile(): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                body.profileUser?.let { u ->
                    tokenManager.saveUserData(u.id, u.displayName, u.email, u.phone)
                }
                NetworkResult.Success(body)
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Failed to fetch profile (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: Profile unavailable offline.", throwable = e)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to load profile", throwable = e)
        }
    }

    suspend fun logout(): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.logout()
        } catch (_: Exception) {
            // Local cleanup continues even if network logout fails
        }
        tokenManager.clear()
        NetworkResult.Success(Unit)
    }

    private fun parseErrorMessage(rawError: String?, defaultMsg: String): String {
        if (rawError.isNullOrBlank()) return defaultMsg
        return try {
            val json = JsonParser.parseString(rawError).asJsonObject
            val messages = mutableListOf<String>()
            if (json.has("errors") && json.get("errors").isJsonObject) {
                val errorsObj = json.getAsJsonObject("errors")
                for (entry in errorsObj.entrySet()) {
                    if (entry.value.isJsonArray) {
                        for (elem in entry.value.asJsonArray) {
                            messages.add(elem.asString)
                        }
                    } else if (entry.value.isJsonPrimitive) {
                        messages.add(entry.value.asString)
                    }
                }
            }
            if (messages.isNotEmpty()) {
                messages.joinToString(" ")
            } else if (json.has("message") && !json.get("message").isJsonNull) {
                json.get("message").asString
            } else {
                defaultMsg
            }
        } catch (_: Exception) {
            defaultMsg
        }
    }
}
