package com.yalu.india.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yalu.india.core.NetworkResult
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.network.models.AdSummaryDto
import com.yalu.india.network.models.CategoryDto
import com.yalu.india.network.models.CityDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val adRepository: AdRepository
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<NetworkResult<List<CategoryDto>>>(NetworkResult.Loading)
    val categoriesState: StateFlow<NetworkResult<List<CategoryDto>>> = _categoriesState.asStateFlow()

    private val _featuredAdsState = MutableStateFlow<NetworkResult<List<AdSummaryDto>>>(NetworkResult.Loading)
    val featuredAdsState: StateFlow<NetworkResult<List<AdSummaryDto>>> = _featuredAdsState.asStateFlow()

    private val _latestAdsState = MutableStateFlow<NetworkResult<List<AdSummaryDto>>>(NetworkResult.Loading)
    val latestAdsState: StateFlow<NetworkResult<List<AdSummaryDto>>> = _latestAdsState.asStateFlow()

    private val _selectedCity = MutableStateFlow(adRepository.getSelectedCity())
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _citiesList = MutableStateFlow<List<CityDto>>(emptyList())
    val citiesList: StateFlow<List<CityDto>> = _citiesList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        fetchCities()
        fetchCategories()
        fetchFeaturedAds()
        fetchLatestAds()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchCategories()
            fetchFeaturedAds()
            fetchLatestAds()
            _isRefreshing.value = false
        }
    }

    fun selectCity(cityName: String) {
        val safeCity = cityName.ifBlank { "All India" }
        _selectedCity.value = safeCity
        adRepository.setSelectedCity(safeCity)
        // Refresh ads filtered by city
        fetchFeaturedAds()
        fetchLatestAds()
    }

    private fun fetchCities() {
        viewModelScope.launch {
            when (val res = adRepository.getCities()) {
                is NetworkResult.Success -> _citiesList.value = res.data
                else -> Unit
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _categoriesState.value = NetworkResult.Loading
            _categoriesState.value = adRepository.getCategories()
        }
    }

    private fun fetchFeaturedAds() {
        viewModelScope.launch {
            _featuredAdsState.value = NetworkResult.Loading
            val city = _selectedCity.value
            when (val res = adRepository.getAds(city = city, isFeatured = true, perPage = 8)) {
                is NetworkResult.Success -> {
                    val ads = res.data.safeData
                    _featuredAdsState.value = NetworkResult.Success(ads)
                }
                is NetworkResult.Error -> {
                    val msg = res.message.ifBlank { "Unable to load featured listings." }
                    _featuredAdsState.value = NetworkResult.Error(msg, res.statusCode)
                }
                else -> Unit
            }
        }
    }

    private fun fetchLatestAds() {
        viewModelScope.launch {
            _latestAdsState.value = NetworkResult.Loading
            val city = _selectedCity.value
            when (val res = adRepository.getAds(city = city, sort = "latest", perPage = 16)) {
                is NetworkResult.Success -> {
                    val ads = res.data.safeData
                    _latestAdsState.value = NetworkResult.Success(ads)
                }
                is NetworkResult.Error -> {
                    val msg = res.message.ifBlank { "Unable to load fresh recommendations." }
                    _latestAdsState.value = NetworkResult.Error(msg, res.statusCode)
                }
                else -> Unit
            }
        }
    }
}
