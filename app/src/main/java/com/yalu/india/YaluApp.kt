package com.yalu.india

import android.app.Application
import com.yalu.india.core.TokenManager
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.data.repository.AuthRepository
import com.yalu.india.network.RetrofitClient
import com.yalu.india.network.YaluApiService

class YaluApp : Application() {

    lateinit var tokenManager: TokenManager
        private set

    lateinit var apiService: YaluApiService
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var adRepository: AdRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        tokenManager = TokenManager(applicationContext)
        apiService = RetrofitClient.getApiService(tokenManager)
        authRepository = AuthRepository(apiService, tokenManager)
        adRepository = AdRepository(apiService, tokenManager)
    }

    companion object {
        lateinit var instance: YaluApp
            private set
    }
}
