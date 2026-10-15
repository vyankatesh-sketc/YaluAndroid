package com.yalu.india.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.yalu.india.data.repository.AdRepository
import com.yalu.india.data.repository.AuthRepository
import com.yalu.india.ui.components.YaluBottomNavigationBar
import com.yalu.india.ui.screens.auth.AuthScreen
import com.yalu.india.ui.screens.auth.AuthViewModel
import com.yalu.india.ui.screens.categories.CategoriesScreen
import com.yalu.india.ui.screens.categories.CategoriesViewModel
import com.yalu.india.ui.screens.details.AdDetailScreen
import com.yalu.india.ui.screens.details.AdDetailViewModel
import com.yalu.india.ui.screens.favorites.FavoritesScreen
import com.yalu.india.ui.screens.favorites.FavoritesViewModel
import com.yalu.india.ui.screens.home.HomeScreen
import com.yalu.india.ui.screens.home.HomeViewModel
import com.yalu.india.ui.screens.profile.ProfileScreen
import com.yalu.india.ui.screens.profile.ProfileViewModel
import com.yalu.india.ui.screens.search.SearchScreen
import com.yalu.india.ui.screens.search.SearchViewModel

@Composable
fun YaluNavHost(
    navController: NavHostController,
    authRepository: AuthRepository,
    adRepository: AdRepository,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavRoutes.Home.route,
        NavRoutes.Categories.route,
        NavRoutes.Favorites.route,
        NavRoutes.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                YaluBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(NavRoutes.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route,
            modifier = modifier.padding(innerPadding)
        ) {
            // Home Destination
            composable(NavRoutes.Home.route) {
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel {
                    HomeViewModel(adRepository)
                }
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCategory = { catId ->
                        navController.navigate(NavRoutes.Search.createRoute(categoryId = catId))
                    },
                    onNavigateToAdDetail = { adId ->
                        navController.navigate(NavRoutes.AdDetail.createRoute(adId))
                    },
                    onNavigateToSearch = {
                        navController.navigate(NavRoutes.Search.createRoute())
                    },
                    onNavigateToPostAd = {
                        if (authRepository.isLoggedIn.value) {
                            // User is logged in, navigate to Post Ad flow
                            navController.navigate(NavRoutes.Profile.route)
                        } else {
                            navController.navigate(NavRoutes.Auth.route)
                        }
                    }
                )
            }

            // Categories Destination
            composable(NavRoutes.Categories.route) {
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel {
                    CategoriesViewModel(adRepository)
                }
                CategoriesScreen(
                    viewModel = viewModel,
                    onCategorySelected = { catId ->
                        navController.navigate(NavRoutes.Search.createRoute(categoryId = catId))
                    }
                )
            }

            // Favorites Destination
            composable(NavRoutes.Favorites.route) {
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel {
                    FavoritesViewModel(adRepository, authRepository)
                }
                FavoritesScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = {
                        navController.navigate(NavRoutes.Auth.route)
                    },
                    onAdClick = { adId ->
                        navController.navigate(NavRoutes.AdDetail.createRoute(adId))
                    }
                )
            }

            // Profile Destination
            composable(NavRoutes.Profile.route) {
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel {
                    ProfileViewModel(authRepository, adRepository)
                }
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = {
                        navController.navigate(NavRoutes.Auth.route)
                    },
                    onNavigateToFavorites = {
                        navController.navigate(NavRoutes.Favorites.route)
                    },
                    onNavigateToMyAds = {
                        navController.navigate(NavRoutes.Profile.route)
                    }
                )
            }

            // Search Destination
            composable(
                route = NavRoutes.Search.route,
                arguments = listOf(
                    navArgument("keyword") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    },
                    navArgument("categoryId") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val keyword = backStackEntry.arguments?.getString("keyword") ?: ""
                val categoryIdStr = backStackEntry.arguments?.getString("categoryId")
                val categoryId = categoryIdStr?.toLongOrNull()

                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel {
                    SearchViewModel(
                        adRepository = adRepository,
                        initialKeyword = keyword,
                        initialCategoryId = categoryId
                    )
                }
                SearchScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onAdClick = { adId ->
                        navController.navigate(NavRoutes.AdDetail.createRoute(adId))
                    }
                )
            }

            // Ad Detail Destination
            composable(
                route = NavRoutes.AdDetail.route,
                arguments = listOf(
                    navArgument("adId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val adId = backStackEntry.arguments?.getString("adId") ?: ""
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel {
                    AdDetailViewModel(adRepository, adId)
                }
                AdDetailScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Auth (Login / Register) Destination
            composable(NavRoutes.Auth.route) {
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel {
                    AuthViewModel(authRepository)
                }
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
