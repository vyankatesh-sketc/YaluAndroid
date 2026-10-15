package com.yalu.india.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yalu.india.core.NetworkResult
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.network.models.AdDetailDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdDetailViewModel(
    private val adRepository: AdRepository,
    private val adId: String
) : ViewModel() {

    private val _adDetailState = MutableStateFlow<NetworkResult<AdDetailDto>>(NetworkResult.Loading)
    val adDetailState: StateFlow<NetworkResult<AdDetailDto>> = _adDetailState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _adDetailState.value = NetworkResult.Loading
            _adDetailState.value = adRepository.getAdDetail(adId)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val next = !_isFavorite.value
            _isFavorite.value = next
            adRepository.toggleFavorite(adId)
        }
    }
}
