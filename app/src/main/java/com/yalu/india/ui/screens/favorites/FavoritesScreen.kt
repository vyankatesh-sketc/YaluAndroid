package com.yalu.india.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalu.india.core.NetworkResult
import com.yalu.india.ui.components.AdCard
import com.yalu.india.ui.components.EmptyStateView
import com.yalu.india.ui.components.ErrorStateView
import com.yalu.india.ui.components.LoadingView
import com.yalu.india.ui.theme.YaluNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToLogin: () -> Unit,
    onAdClick: (String) -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val favoritesState by viewModel.favoritesState.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.loadFavorites()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Favorites", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = YaluNavy)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!isLoggedIn) {
                // Logged-out Prompt
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = YaluNavy,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Save ads you love",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sign in to your Yalu India account to bookmark listings and access them across all your devices.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToLogin,
                        colors = ButtonDefaults.buttonColors(containerColor = YaluNavy),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Log In or Register", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                when (val state = favoritesState) {
                    is NetworkResult.Loading -> LoadingView(message = "Loading favorites…")
                    is NetworkResult.Success -> {
                        val ads = state.data.orEmpty()
                        if (ads.isNotEmpty()) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(ads) { ad ->
                                    AdCard(
                                        ad = ad,
                                        isFavorite = true,
                                        onClick = { onAdClick(ad.id.orEmpty()) }
                                    )
                                }
                            }
                        } else {
                            EmptyStateView(
                                title = "No favorites saved yet",
                                subtitle = "Browse the marketplace and tap the heart icon on any ad to bookmark it.",
                                icon = Icons.Outlined.FavoriteBorder
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        ErrorStateView(
                            message = state.message,
                            onRetry = { viewModel.loadFavorites() }
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}
