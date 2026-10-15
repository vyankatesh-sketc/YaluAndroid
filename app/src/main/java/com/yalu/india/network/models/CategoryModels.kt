package com.yalu.india.network.models

import com.google.gson.annotations.SerializedName

data class SubcategoryDto(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("parent_id") val parentId: Long? = null,
    @SerializedName("ads_count") val adsCount: Int? = 0
) {
    val displayTitle: String
        get() = name?.takeIf { it.isNotBlank() }
            ?: title?.takeIf { it.isNotBlank() }
            ?: "Subcategory"
}

data class CategoryDto(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("subcategories") val subcategories: List<SubcategoryDto>? = emptyList()
) {
    val displayTitle: String
        get() = name?.takeIf { it.isNotBlank() }
            ?: title?.takeIf { it.isNotBlank() }
            ?: categoryName?.takeIf { it.isNotBlank() }
            ?: "Category"

    val safeSubcategories: List<SubcategoryDto>
        get() = subcategories ?: emptyList()
}

data class CategoriesResponse(
    @SerializedName("categories") val categories: List<CategoryDto>? = emptyList(),
    @SerializedName("data") val data: List<CategoryDto>? = emptyList()
) {
    val safeCategories: List<CategoryDto>
        get() = when {
            !data.isNullOrEmpty() -> data
            !categories.isNullOrEmpty() -> categories
            else -> emptyList()
        }
}
