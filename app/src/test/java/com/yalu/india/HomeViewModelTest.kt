package com.yalu.india

import com.yalu.india.core.NetworkResult
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.network.models.AdsListResponse
import com.yalu.india.network.models.CategoryDto
import com.yalu.india.network.models.CityDto
import com.yalu.india.ui.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val adRepository: AdRepository = mock()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(adRepository.getSelectedCity()).thenReturn("Bangalore")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectCity_updatesStateAndPersists() = runTest {
        whenever(adRepository.getCities()).thenReturn(NetworkResult.Success(listOf(CityDto(1, "Mumbai"))))
        whenever(adRepository.getCategories()).thenReturn(NetworkResult.Success(emptyList()))
        whenever(
            adRepository.getAds(
                categoryId = anyOrNull(),
                city = anyOrNull(),
                keyword = anyOrNull(),
                minPrice = anyOrNull(),
                maxPrice = anyOrNull(),
                isFeatured = anyOrNull(),
                sort = anyOrNull(),
                perPage = anyOrNull(),
                page = anyOrNull()
            )
        ).thenReturn(
            NetworkResult.Success(AdsListResponse(success = true, data = emptyList()))
        )

        viewModel = HomeViewModel(adRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectCity("Mumbai")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Mumbai", viewModel.selectedCity.value)
        verify(adRepository).setSelectedCity("Mumbai")
    }
}
