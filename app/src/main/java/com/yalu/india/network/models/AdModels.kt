package com.yalu.india.network.models

import com.google.gson.annotations.SerializedName

data class AdSummaryDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("price") val price: Double? = 0.0,
    @SerializedName("formatted_price") val formattedPrice: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("category_id") val categoryId: Long? = null,
    @SerializedName("is_featured") val isFeatured: Boolean = false,
    @SerializedName("is_negotiable") val isNegotiable: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null
) {
    val displayTitle: String
        get() = title.orEmpty().ifBlank { "Untitled Listing" }

    val displayPrice: String
        get() = formattedPrice.orEmpty().ifBlank { "₹ ${(price ?: 0.0).toInt()}" }

    val displayLocation: String
        get() = location.orEmpty().ifBlank { "India" }
}

data class SellerDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = "Verified Seller",
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("whatsapp") val whatsapp: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("member_since") val memberSince: String? = null,
    @SerializedName("is_verified") val isVerified: Boolean = true
) {
    val displayName: String
        get() = name.orEmpty().ifBlank { "Verified Seller" }
}

data class AdDetailDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("price") val price: Double? = 0.0,
    @SerializedName("formatted_price") val formattedPrice: String? = null,
    @SerializedName("description") val description: String? = "",
    @SerializedName("location") val location: String? = null,
    @SerializedName("images") val images: List<String>? = emptyList(),
    @SerializedName("category") val category: String? = null,
    @SerializedName("category_id") val categoryId: Long? = null,
    @SerializedName("is_featured") val isFeatured: Boolean = false,
    @SerializedName("is_negotiable") val isNegotiable: Boolean = false,
    @SerializedName("views_count") val viewsCount: Int = 1,
    @SerializedName("seller") val seller: SellerDto? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("share_url") val shareUrl: String? = null
) {
    val displayTitle: String
        get() = title.orEmpty().ifBlank { "Untitled Listing" }

    val displayPrice: String
        get() = formattedPrice.orEmpty().ifBlank { "₹ ${(price ?: 0.0).toInt()}" }

    val displayLocation: String
        get() = location.orEmpty().ifBlank { "India" }

    val displayDescription: String
        get() = description.orEmpty().ifBlank { "No detailed description provided by the seller." }

    val safeImages: List<String>
        get() = images ?: emptyList()
}

data class CityDto(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("name") val name: String? = null,
    @SerializedName("slug") val slug: String? = null
) {
    val displayName: String
        get() = name.orEmpty().ifBlank { "Unknown City" }
}

data class AdsListResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("data") val data: List<AdSummaryDto>? = emptyList(),
    @SerializedName("pagination") val pagination: PaginationMeta? = null
) {
    val safeData: List<AdSummaryDto>
        get() = data ?: emptyList()
}

data class AdDetailResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("data") val data: AdDetailDto? = null,
    @SerializedName("related") val related: List<AdSummaryDto>? = emptyList(),
    @SerializedName("message") val message: String? = null
) {
    val safeRelated: List<AdSummaryDto>
        get() = related ?: emptyList()
}

data class FavoriteToggleResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
    @SerializedName("message") val message: String? = null
)
