package com.yalu.india.ui.screens.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yalu.india.core.NetworkResult
import com.yalu.india.network.models.AdDetailDto
import com.yalu.india.ui.components.ErrorStateView
import com.yalu.india.ui.components.LoadingView
import com.yalu.india.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdDetailScreen(
    viewModel: AdDetailViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val detailState by viewModel.adDetailState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listing Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isFavorite) YaluRed else Color.Unspecified
                        )
                    }
                    IconButton(onClick = {
                        val ad = (detailState as? NetworkResult.Success)?.data
                        if (ad != null) {
                            shareListing(context, ad)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            val ad = (detailState as? NetworkResult.Success)?.data
            if (ad != null) {
                BottomContactBar(
                    ad = ad,
                    onCallClick = { phone -> launchDialer(context, phone) },
                    onWhatsAppClick = { phone, title -> launchWhatsApp(context, phone, title) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = detailState) {
                is NetworkResult.Loading -> LoadingView(message = "Loading listing details…")
                is NetworkResult.Success -> {
                    AdDetailContent(ad = state.data)
                }
                is NetworkResult.Error -> {
                    ErrorStateView(
                        message = state.message,
                        onRetry = { viewModel.loadDetail() }
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun AdDetailContent(ad: AdDetailDto) {
    val images = ad.safeImages
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Image Gallery Slider
        item {
            if (images.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(images) { imgUrl ->
                        AsyncImage(
                            model = imgUrl,
                            contentDescription = ad.displayTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .height(280.dp)
                        )
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }

        // Price & Overview Card
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = ad.displayPrice,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = YaluNavy
                        )

                        if (ad.isNegotiable) {
                            Surface(
                                color = YaluPriceGreen.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Negotiable",
                                    color = YaluPriceGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = ad.displayTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = ad.displayLocation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!ad.createdAt.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = ad.createdAt.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Seller Profile Card
        item {
            val seller = ad.seller
            if (seller != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = seller.avatar ?: "https://www.yalu.in/pwa/icons/icon-192x192.png",
                            contentDescription = seller.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = seller.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (seller.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = YaluVerifiedBadge,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = seller.memberSince.orEmpty().ifBlank { "Verified Yalu India Member" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Description Card
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = ad.displayDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomContactBar(
    ad: AdDetailDto,
    onCallClick: (String) -> Unit,
    onWhatsAppClick: (String, String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Call Button
            OutlinedButton(
                onClick = {
                    val phone = ad.seller?.phone.orEmpty()
                    onCallClick(phone)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = "Call", tint = YaluNavy)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Call", color = YaluNavy, fontWeight = FontWeight.Bold)
            }

            // WhatsApp Button
            Button(
                onClick = {
                    val phone = ad.seller?.whatsapp.orEmpty().ifBlank { ad.seller?.phone.orEmpty() }
                    onWhatsAppClick(phone, ad.displayTitle)
                },
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YaluWhatsAppGreen)
            ) {
                Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun launchDialer(context: Context, phone: String) {
    if (phone.isBlank()) {
        Toast.makeText(context, "Phone number not provided by seller", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "No dialer application found", Toast.LENGTH_SHORT).show()
    }
}

private fun launchWhatsApp(context: Context, phone: String, adTitle: String) {
    if (phone.isBlank()) {
        Toast.makeText(context, "WhatsApp contact not provided", Toast.LENGTH_SHORT).show()
        return
    }
    val cleanPhone = phone.replace("+", "").replace("-", "").replace(" ", "")
    val msg = "Hi, I am interested in your listing on Yalu India: $adTitle"
    val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(msg)}"
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
    }
}

private fun shareListing(context: Context, ad: AdDetailDto) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, ad.displayTitle)
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out this listing on Yalu India:\n${ad.displayTitle}\nPrice: ${ad.displayPrice}\n${ad.shareUrl ?: "https://www.yalu.in"}"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Listing via"))
}
