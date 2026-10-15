package com.yalu.india.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Categories : NavRoutes("categories")
    data object Favorites : NavRoutes("favorites")
    data object Profile : NavRoutes("profile")
    data object Auth : NavRoutes("auth")

    data object AdDetail : NavRoutes("ad/{adId}") {
        fun createRoute(adId: String) = "ad/$adId"
    }

    data object Search : NavRoutes("search?keyword={keyword}&categoryId={categoryId}") {
        fun createRoute(keyword: String = "", categoryId: Long? = null): String {
            val kw = if (keyword.isNotBlank()) "keyword=$keyword" else ""
            val cat = if (categoryId != null && categoryId > 0) "categoryId=$categoryId" else ""
            val queryParams = listOf(kw, cat).filter { it.isNotEmpty() }.joinToString("&")
            return if (queryParams.isNotEmpty()) "search?$queryParams" else "search"
        }
    }
}
