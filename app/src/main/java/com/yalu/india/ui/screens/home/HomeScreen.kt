package com.yalu.india.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalu.india.core.NetworkResult
import com.yalu.india.ui.components.*
import com.yalu.india.ui.theme.YaluNavy
import com.yalu.india.ui.theme.YaluNavyDark
import com.yalu.india.ui.theme.YaluRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCategory: (Long) -> Unit,
    onNavigateToAdDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToPostAd: () -> Unit
) {
    val categoriesState by viewModel.categoriesState.collectAsState()
    val featuredAdsState by viewModel.featuredAdsState.collectAsState()
    val latestAdsState by viewModel.latestAdsState.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val citiesList by viewModel.citiesList.collectAsState()

    var showCityDialog by remember { mutableStateOf(false) }

    val currentCity = selectedCity.ifBlank { "All India" }

    if (showCityDialog) {
        CitySelectionDialog(
            cities = citiesList,
            selectedCity = currentCity,
            onCitySelected = { viewModel.selectCity(it) },
            onDismiss = { showCityDialog = false }
        )
    }

    Scaffold(
        topBar = {
            YaluHomeTopBar(
                selectedCity = currentCity,
                onCityClick = { showCityDialog = true },
                onSearchClick = onNavigateToSearch
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPostAd,
                containerColor = YaluRed,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Post Ad") },
                text = { Text("Post Ad", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Hero Promo Banner
            item {
                HeroPromoBanner(onPostAdClick = onNavigateToPostAd)
            }

            // 2. Popular Categories
            item {
                SectionHeader(title = "Explore Categories", onActionClick = null)
                when (val state = categoriesState) {
                    is NetworkResult.Loading -> LoadingView(message = "Loading categories…")
                    is NetworkResult.Success -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.data) { cat ->
                                CategoryCircleItem(
                                    category = cat,
                                    onClick = { onNavigateToCategory(cat.id) }
                                )
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        ErrorStateView(
                            message = state.message,
                            onRetry = { viewModel.refresh() }
                        )
                    }
                    else -> Unit
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Quick Travel & Services Card
            item {
                QuickServicesBanner(
                    onMetroClick = { onNavigateToSearch() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Featured Listings
            item {
                SectionHeader(title = "Featured Listings", onActionClick = onNavigateToSearch)
                when (val state = featuredAdsState) {
                    is NetworkResult.Loading -> LoadingView(message = "Loading featured ads…")
                    is NetworkResult.Success -> {
                        val ads = state.data.orEmpty()
                        if (ads.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(ads) { ad ->
                                    AdCard(
                                        ad = ad,
                                        onClick = { onNavigateToAdDetail(ad.id.orEmpty()) },
                                        modifier = Modifier.width(220.dp)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No featured listings available in $currentCity right now.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        Text(
                            text = state.message.ifBlank { "Unable to load featured listings." },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    else -> Unit
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 5. Fresh Recommendations / Latest Ads
            item {
                SectionHeader(title = "Fresh Recommendations", onActionClick = onNavigateToSearch)
            }

            when (val state = latestAdsState) {
                is NetworkResult.Loading -> {
                    item { LoadingView(message = "Fetching latest ads…") }
                }
                is NetworkResult.Success -> {
                    val ads = state.data.orEmpty()
                    if (ads.isNotEmpty()) {
                        val chunked = ads.chunked(2)
                        items(chunked) { rowAds ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowAds.forEach { ad ->
                                    AdCard(
                                        ad = ad,
                                        onClick = { onNavigateToAdDetail(ad.id.orEmpty()) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowAds.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    } else {
                        item {
                            EmptyStateView(
                                title = "No listings found in $currentCity",
                                subtitle = "Be the first to post an ad in this city!"
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    item {
                        ErrorStateView(
                            message = state.message,
                            onRetry = { viewModel.refresh() }
                        )
                    }
                }
                else -> Unit
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onActionClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (onActionClick != null) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = YaluRed,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Composable
private fun HeroPromoBanner(onPostAdClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(YaluNavy, YaluNavyDark)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = YaluRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INDIA\'S TRUSTED MARKETPLACE",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sell Fast. Buy Smart.",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Post free ads across verified Indian buyers with instant WhatsApp contact.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onPostAdClick,
                    colors = ButtonDefaults.buttonColors(containerColor = YaluRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Post Free Ad Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickServicesBanner(onMetroClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = YaluNavy,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Train,
                            contentDescription = "Metro",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Metro Tickets & Travel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Hyderabad, Delhi, Bangalore, Mumbai",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TextButton(onClick = onMetroClick) {
                Text("Explore", color = YaluNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
