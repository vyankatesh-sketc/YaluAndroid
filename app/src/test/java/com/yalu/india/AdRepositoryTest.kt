package com.yalu.india

import com.yalu.india.core.NetworkResult
import com.yalu.india.core.TokenManager
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.network.YaluApiService
import com.yalu.india.network.models.AdSummaryDto
import com.yalu.india.network.models.AdsListResponse
import com.yalu.india.network.models.CategoriesResponse
import com.yalu.india.network.models.CategoryDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AdRepositoryTest {

    private val apiService: YaluApiService = mock()
    private val tokenManager: TokenManager = mock()
    private lateinit var adRepository: AdRepository

    @Before
    fun setup() {
        whenever(tokenManager.getSelectedCity()).thenReturn("Hyderabad")
        adRepository = AdRepository(apiService, tokenManager)
    }

    @Test
    fun getCategories_success_returnsList() = runTest {
        val sampleCats = listOf(
            CategoryDto(id = 1, title = "Mobiles", slug = "mobiles")
        )
        whenever(apiService.getCategories()).thenReturn(Response.success(CategoriesResponse(sampleCats)))

        val result = adRepository.getCategories()

        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(1, data.size)
        assertEquals("Mobiles", data[0].displayTitle)
    }

    @Test
    fun getAds_success_returnsAdsList() = runTest {
        val sampleAds = listOf(
            AdSummaryDto(
                id = "1",
                title = "Test Phone",
                price = 15000.0,
                formattedPrice = "₹ 15,000",
                location = "Hyderabad"
            )
        )
        val response = AdsListResponse(success = true, data = sampleAds)

        whenever(
            apiService.getAds(
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
        ).thenReturn(Response.success(response))

        val result = adRepository.getAds(city = "Hyderabad")

        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(1, data.safeData.size)
        assertEquals("Test Phone", data.safeData[0].displayTitle)
    }
}
