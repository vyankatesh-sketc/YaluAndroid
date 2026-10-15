package com.yalu.india.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yalu.india.core.NetworkResult
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.network.models.CategoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val adRepository: AdRepository
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<NetworkResult<List<CategoryDto>>>(NetworkResult.Loading)
    val categoriesState: StateFlow<NetworkResult<List<CategoryDto>>> = _categoriesState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = NetworkResult.Loading
            _categoriesState.value = adRepository.getCategories()
        }
    }
}
