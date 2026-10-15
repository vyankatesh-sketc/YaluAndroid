package com.yalu.india.network

import com.yalu.india.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface YaluApiService {

    // --- Authentication ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("auth/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @POST("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // --- Public Metadata & Listings ---
    @GET("categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("cities")
    suspend fun getCities(): Response<ApiResponse<List<CityDto>>>

    @GET("ads")
    suspend fun getAds(
        @Query("category_id") categoryId: Long? = null,
        @Query("city") city: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("is_featured") isFeatured: Boolean? = null,
        @Query("sort") sort: String? = null, // price_low, price_high, popular, latest
        @Query("per_page") perPage: Int? = 16,
        @Query("page") page: Int? = 1
    ): Response<AdsListResponse>

    @GET("ads/{id}")
    suspend fun getAdDetail(
        @Path("id") id: String
    ): Response<AdDetailResponse>

    // --- Authenticated User Operations ---
    @GET("user/ads")
    suspend fun getMyAds(
        @Query("status") status: String? = "all"
    ): Response<AdsListResponse>

    @GET("user/favorites")
    suspend fun getMyFavorites(): Response<ApiResponse<List<AdSummaryDto>>>

    @POST("ads/{id}/favorite")
    suspend fun toggleFavorite(
        @Path("id") id: String
    ): Response<FavoriteToggleResponse>
}
