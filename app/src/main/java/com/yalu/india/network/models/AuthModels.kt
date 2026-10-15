package com.yalu.india.network.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("login") val login: String, // email or phone
    @SerializedName("email") val email: String = login,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String = password
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("whatsapp_number") val whatsappNumber: String? = null
)

data class UserDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("whatsapp") val whatsapp: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("member_since") val memberSince: String? = null,
    @SerializedName("active_ads_count") val activeAdsCount: Int = 0,
    @SerializedName("favorites_count") val favoritesCount: Int = 0
) {
    val displayName: String
        get() = name.orEmpty().ifBlank { "User" }
}

data class AuthDataPayload(
    @SerializedName("token") val token: String? = null,
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("user") val user: UserDto? = null
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("data") val data: AuthDataPayload? = null
) {
    val authToken: String?
        get() = token?.takeIf { it.isNotBlank() }
            ?: accessToken?.takeIf { it.isNotBlank() }
            ?: data?.token?.takeIf { it.isNotBlank() }
            ?: data?.accessToken?.takeIf { it.isNotBlank() }

    val authUser: UserDto?
        get() = user ?: data?.user
}

data class ProfileResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("data") val data: UserDto? = null
) {
    val profileUser: UserDto?
        get() = user ?: data
}
