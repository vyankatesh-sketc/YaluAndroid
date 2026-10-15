package com.yalu.india.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalu.india.core.NetworkResult
import com.yalu.india.ui.components.AdCard
import com.yalu.india.ui.components.EmptyStateView
import com.yalu.india.ui.components.ErrorStateView
import com.yalu.india.ui.components.LoadingView
import com.yalu.india.ui.theme.YaluNavy
import com.yalu.india.ui.theme.YaluRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onAdClick: (String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val sort by viewModel.sort.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Surface(color = YaluNavy, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    TextField(
                        value = query,
                        onValueChange = { viewModel.setQuery(it) },
                        placeholder = { Text("Search by brand, item, model…", fontSize = 14.sp) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setQuery(""); viewModel.search() }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            } else {
                                IconButton(onClick = { focusManager.clearFocus(); viewModel.search() }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus()
                            viewModel.search()
                        }),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Category Filter Chips
            if (categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { viewModel.setCategory(null) },
                            label = { Text("All Categories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = YaluNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = { viewModel.setCategory(cat.id) },
                            label = { Text(cat.displayTitle) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = YaluNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Sort Selector Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Sort:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SortChip(label = "Latest", isSelected = sort == "latest", onClick = { viewModel.setSort("latest") })
                SortChip(label = "Price: Low", isSelected = sort == "price_low", onClick = { viewModel.setSort("price_low") })
                SortChip(label = "Price: High", isSelected = sort == "price_high", onClick = { viewModel.setSort("price_high") })
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Results Container
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = searchResults) {
                    is NetworkResult.Loading -> LoadingView(message = "Searching listings…")
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
                                        onClick = { onAdClick(ad.id.orEmpty()) }
                                    )
                                }
                            }
                        } else {
                            EmptyStateView(
                                title = "No listings found",
                                subtitle = "We couldn\'t find any match for your query. Try broadening your terms."
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        ErrorStateView(
                            message = state.message,
                            onRetry = { viewModel.search() }
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) YaluRed else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clickable { onClick() }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
