package com.yalu.india.network

import com.yalu.india.core.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Accept", "application/json")
            .header("User-Agent", "YaluAndroidApp/1.0")

        val token = tokenManager.getToken()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
