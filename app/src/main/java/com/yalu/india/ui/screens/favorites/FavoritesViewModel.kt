package com.yalu.india.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yalu.india.core.NetworkResult
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.data.repository.AuthRepository
import com.yalu.india.network.models.AdSummaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val adRepository: AdRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn

    private val _favoritesState = MutableStateFlow<NetworkResult<List<AdSummaryDto>>>(NetworkResult.Idle)
    val favoritesState: StateFlow<NetworkResult<List<AdSummaryDto>>> = _favoritesState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        if (!isLoggedIn.value) {
            _favoritesState.value = NetworkResult.Idle
            return
        }
        viewModelScope.launch {
            _favoritesState.value = NetworkResult.Loading
            _favoritesState.value = adRepository.getFavorites()
        }
    }
}
