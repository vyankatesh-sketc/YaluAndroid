package com.yalu.india.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yalu.india.ui.navigation.NavRoutes
import com.yalu.india.ui.theme.YaluNavy
import com.yalu.india.ui.theme.YaluRed

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem(NavRoutes.Home.route, "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Categories : BottomNavItem(NavRoutes.Categories.route, "Categories", Icons.Filled.Category, Icons.Outlined.Category)
    data object Favorites : BottomNavItem(NavRoutes.Favorites.route, "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    data object Profile : BottomNavItem(NavRoutes.Profile.route, "Account", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun YaluBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Categories,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = YaluRed,
                    selectedTextColor = YaluNavy,
                    indicatorColor = YaluRed.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
