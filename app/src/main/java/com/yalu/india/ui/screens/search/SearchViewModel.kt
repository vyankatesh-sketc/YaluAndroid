package com.yalu.india.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yalu.india.core.NetworkResult
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.network.models.AdSummaryDto
import com.yalu.india.network.models.CategoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val adRepository: AdRepository,
    initialKeyword: String = "",
    initialCategoryId: Long? = null
) : ViewModel() {

    private val _query = MutableStateFlow(initialKeyword)
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(initialCategoryId)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories.asStateFlow()

    private val _sort = MutableStateFlow("latest")
    val sort: StateFlow<String> = _sort.asStateFlow()

    private val _searchResults = MutableStateFlow<NetworkResult<List<AdSummaryDto>>>(NetworkResult.Idle)
    val searchResults: StateFlow<NetworkResult<List<AdSummaryDto>>> = _searchResults.asStateFlow()

    init {
        loadCategories()
        search()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val res = adRepository.getCategories()) {
                is NetworkResult.Success -> _categories.value = res.data
                else -> Unit
            }
        }
    }

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun setCategory(categoryId: Long?) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == categoryId) null else categoryId
        search()
    }

    fun setSort(newSort: String) {
        _sort.value = newSort
        search()
    }

    fun search() {
        viewModelScope.launch {
            _searchResults.value = NetworkResult.Loading
            val city = adRepository.getSelectedCity()
            val res = adRepository.getAds(
                categoryId = _selectedCategoryId.value,
                city = city,
                keyword = _query.value,
                sort = _sort.value,
                perPage = 24
            )
            when (res) {
                is NetworkResult.Success -> {
                    _searchResults.value = NetworkResult.Success(res.data.safeData)
                }
                is NetworkResult.Error -> {
                    _searchResults.value = NetworkResult.Error(res.message, res.statusCode)
                }
                else -> Unit
            }
        }
    }
}
