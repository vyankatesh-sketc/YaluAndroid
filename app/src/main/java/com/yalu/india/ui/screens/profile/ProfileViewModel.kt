package com.yalu.india.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yalu.india.core.NetworkResult
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.data.repository.AuthRepository
import com.yalu.india.network.models.AdSummaryDto
import com.yalu.india.network.models.ProfileResponse
import com.yalu.india.network.models.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val adRepository: AdRepository
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn

    private val _profileState = MutableStateFlow<NetworkResult<ProfileResponse>>(NetworkResult.Idle)
    val profileState: StateFlow<NetworkResult<ProfileResponse>> = _profileState.asStateFlow()

    private val _myAdsState = MutableStateFlow<NetworkResult<List<AdSummaryDto>>>(NetworkResult.Idle)
    val myAdsState: StateFlow<NetworkResult<List<AdSummaryDto>>> = _myAdsState.asStateFlow()

    fun loadProfile() {
        if (!isLoggedIn.value) return
        viewModelScope.launch {
            _profileState.value = NetworkResult.Loading
            _profileState.value = authRepository.getProfile()
            loadMyAds()
        }
    }

    fun loadMyAds() {
        viewModelScope.launch {
            _myAdsState.value = NetworkResult.Loading
            _myAdsState.value = adRepository.getMyAds()
        }
    }

    fun getCurrentLocalUser(): UserDto? {
        return authRepository.getCurrentUser()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _profileState.value = NetworkResult.Idle
            _myAdsState.value = NetworkResult.Idle
        }
    }
}
