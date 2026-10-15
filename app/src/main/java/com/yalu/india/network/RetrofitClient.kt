package com.yalu.india.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.yalu.india.core.Constants
import com.yalu.india.core.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var apiService: YaluApiService? = null
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(String::class.java, FlexibleStringDeserializer())
        .setLenient()
        .create()

    fun getApiService(tokenManager: TokenManager, baseUrl: String = Constants.BASE_URL): YaluApiService {
        if (apiService == null) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(tokenManager))
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            apiService = retrofit.create(YaluApiService::class.java)
        }
        return apiService!!
    }

    /**
     * For testing or reconfiguration
     */
    fun resetForTesting(customService: YaluApiService) {
        apiService = customService
    }
}
