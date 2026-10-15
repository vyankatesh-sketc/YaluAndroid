package com.yalu.india.data.repository

import com.google.gson.JsonParser
import com.yalu.india.core.NetworkResult
import com.yalu.india.core.TokenManager
import com.yalu.india.network.YaluApiService
import com.yalu.india.network.models.AdDetailDto
import com.yalu.india.network.models.AdSummaryDto
import com.yalu.india.network.models.AdsListResponse
import com.yalu.india.network.models.CategoryDto
import com.yalu.india.network.models.CityDto
import com.yalu.india.network.models.FavoriteToggleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AdRepository(
    private val apiService: YaluApiService,
    private val tokenManager: TokenManager
) {

    suspend fun getCategories(): NetworkResult<List<CategoryDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body() != null) {
                val cats = response.body()?.safeCategories ?: emptyList()
                NetworkResult.Success(cats)
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Failed to load categories (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to load categories", throwable = e)
        }
    }

    suspend fun getCities(): NetworkResult<List<CityDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCities()
            if (response.isSuccessful && response.body() != null) {
                val cities = response.body()?.data ?: emptyList()
                NetworkResult.Success(cities)
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Failed to load cities (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to load cities", throwable = e)
        }
    }

    suspend fun getAds(
        categoryId: Long? = null,
        city: String? = null,
        keyword: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        isFeatured: Boolean? = null,
        sort: String? = null,
        perPage: Int = 16,
        page: Int = 1
    ): NetworkResult<AdsListResponse> = withContext(Dispatchers.IO) {
        try {
            val effectiveCity = if (city == "All India" || city.isNullOrBlank()) null else city
            val response = apiService.getAds(
                categoryId = categoryId,
                city = effectiveCity,
                keyword = keyword?.takeIf { it.isNotBlank() },
                minPrice = minPrice,
                maxPrice = maxPrice,
                isFeatured = isFeatured,
                sort = sort,
                perPage = perPage,
                page = page
            )
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                val rawError = response.errorBody()?.string()
                val defaultMsg = "Server error (${response.code()}): Unable to fetch listings right now."
                val errorMsg = parseErrorMessage(rawError, defaultMsg)
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: Unable to connect to Yalu servers. Please check your internet connection.", throwable = e)
        } catch (e: HttpException) {
            NetworkResult.Error("Server error (${e.code()}): ${e.message()}", statusCode = e.code(), throwable = e)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to load listings", throwable = e)
        }
    }

    suspend fun getAdDetail(id: String): NetworkResult<AdDetailDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAdDetail(id)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Unable to load listing details (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to load listing details", throwable = e)
        }
    }

    suspend fun getFavorites(): NetworkResult<List<AdSummaryDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMyFavorites()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()?.data ?: emptyList())
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Unable to fetch favorites (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error: Favorites unavailable offline.", throwable = e)
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to fetch favorites", throwable = e)
        }
    }

    suspend fun toggleFavorite(id: String): NetworkResult<FavoriteToggleResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.toggleFavorite(id)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Failed to update favorite status (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Error updating favorite", throwable = e)
        }
    }

    suspend fun getMyAds(status: String = "all"): NetworkResult<List<AdSummaryDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMyAds(status)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()?.safeData ?: emptyList())
            } else {
                val rawError = response.errorBody()?.string()
                val errorMsg = parseErrorMessage(rawError, "Failed to fetch my ads (${response.code()})")
                NetworkResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Error fetching my ads", throwable = e)
        }
    }

    fun getSelectedCity(): String = tokenManager.getSelectedCity()

    fun setSelectedCity(city: String) {
        tokenManager.saveSelectedCity(city)
    }

    private fun parseErrorMessage(rawError: String?, defaultMsg: String): String {
        if (rawError.isNullOrBlank()) return defaultMsg
        return try {
            val json = JsonParser.parseString(rawError).asJsonObject
            val messages = mutableListOf<String>()
            if (json.has("errors") && json.get("errors").isJsonObject) {
                val errorsObj = json.getAsJsonObject("errors")
                for (entry in errorsObj.entrySet()) {
                    if (entry.value.isJsonArray) {
                        for (elem in entry.value.asJsonArray) {
                            messages.add(elem.asString)
                        }
                    } else if (entry.value.isJsonPrimitive) {
                        messages.add(entry.value.asString)
                    }
                }
            }
            if (messages.isNotEmpty()) {
                messages.joinToString(" ")
            } else if (json.has("message") && !json.get("message").isJsonNull) {
                json.get("message").asString
            } else {
                defaultMsg
            }
        } catch (_: Exception) {
            defaultMsg
        }
    }
}
