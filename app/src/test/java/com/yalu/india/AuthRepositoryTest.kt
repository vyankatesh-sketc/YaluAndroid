package com.yalu.india

import com.yalu.india.core.NetworkResult
import com.yalu.india.core.TokenManager
import com.yalu.india.data.repository.AuthRepository
import com.yalu.india.network.YaluApiService
import com.yalu.india.network.models.AuthResponse
import com.yalu.india.network.models.UserDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    private val apiService: YaluApiService = mock()
    private val tokenManager: TokenManager = mock()
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        whenever(tokenManager.isLoggedInFlow).thenReturn(MutableStateFlow(false))
        authRepository = AuthRepository(apiService, tokenManager)
    }

    @Test
    fun login_success_savesTokenAndReturnsSuccess() = runTest {
        val userDto = UserDto(
            id = "10",
            name = "Test User",
            email = "user@example.com",
            phone = "9876543210"
        )
        val authResponse = AuthResponse(
            success = true,
            message = "Login successful",
            token = "sample_sanctum_token_123",
            user = userDto
        )

        whenever(apiService.login(any())).thenReturn(Response.success(authResponse))

        val result = authRepository.login("user@example.com", "password123")

        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals("sample_sanctum_token_123", data.authToken)
        verify(tokenManager).saveToken("sample_sanctum_token_123")
        verify(tokenManager).saveUserData("10", "Test User", "user@example.com", "9876543210")
    }

    @Test
    fun login_failure_returnsError() = runTest {
        val errorJson = "{\"success\":false,\"message\":\"Invalid email/phone or password.\"}"
        val errorResponseBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.login(any())).thenReturn(Response.error(401, errorResponseBody))

        val result = authRepository.login("wrong@user.com", "wrongpass")

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(401, error.statusCode)
    }

    @Test
    fun logout_clearsTokens() = runTest {
        whenever(apiService.logout()).thenReturn(Response.success(mock()))

        val result = authRepository.logout()

        assertTrue(result is NetworkResult.Success)
        verify(tokenManager).clear()
    }
}
