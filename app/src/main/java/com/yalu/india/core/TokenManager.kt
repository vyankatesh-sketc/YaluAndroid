package com.yalu.india.core

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = createEncryptedSharedPreferences(context)
    
    private val _isLoggedInFlow = MutableStateFlow(!getToken().isNullOrBlank())
    val isLoggedInFlow: StateFlow<Boolean> = _isLoggedInFlow.asStateFlow()

    private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                Constants.PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.w("TokenManager", "EncryptedSharedPreferences failed, falling back to standard prefs", e)
            context.getSharedPreferences("yalu_fallback_prefs", Context.MODE_PRIVATE)
        }
    }

    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_AUTH_TOKEN, token).apply()
        _isLoggedInFlow.value = true
    }

    fun getToken(): String? {
        return prefs.getString(Constants.KEY_AUTH_TOKEN, null)
    }

    fun saveUserData(id: String?, name: String?, email: String?, phone: String?) {
        prefs.edit()
            .putString(Constants.KEY_USER_ID, id.orEmpty())
            .putString(Constants.KEY_USER_NAME, name)
            .putString(Constants.KEY_USER_EMAIL, email)
            .putString(Constants.KEY_USER_PHONE, phone)
            .apply()
    }

    fun saveUserData(id: Long, name: String, email: String?, phone: String?) {
        saveUserData(id.toString(), name, email, phone)
    }

    fun getUserName(): String? = prefs.getString(Constants.KEY_USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(Constants.KEY_USER_EMAIL, null)
    fun getUserPhone(): String? = prefs.getString(Constants.KEY_USER_PHONE, null)
    fun getUserId(): String? = prefs.getString(Constants.KEY_USER_ID, null)

    fun saveSelectedCity(city: String) {
        prefs.edit().putString(Constants.KEY_SELECTED_CITY, city).apply()
    }

    fun getSelectedCity(): String {
        return prefs.getString(Constants.KEY_SELECTED_CITY, "All India") ?: "All India"
    }

    fun clear() {
        prefs.edit().clear().apply()
        _isLoggedInFlow.value = false
    }
}
