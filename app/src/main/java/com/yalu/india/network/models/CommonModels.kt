package com.yalu.india.network.models

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

data class PaginationMeta(
    @SerializedName("current_page") val currentPage: Int = 1,
    @SerializedName("last_page") val lastPage: Int = 1,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("per_page") val perPage: Int = 16
)
