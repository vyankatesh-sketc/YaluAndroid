package com.yalu.india.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yalu.india.core.NetworkResult
import com.yalu.india.ui.theme.YaluNavy
import com.yalu.india.ui.theme.YaluRed
import com.yalu.india.ui.theme.YaluVerifiedBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToMyAds: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.loadProfile()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to sign out from Yalu India on this device?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    }
                ) {
                    Text("Log Out", color = YaluRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = YaluNavy)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            if (!isLoggedIn) {
                // Guest Card
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = YaluNavy.copy(alpha = 0.1f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = YaluNavy,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Join Yalu India Marketplace",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Log in or create a free account to manage your listings, chat with buyers, and save favorites.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onNavigateToLogin,
                            colors = ButtonDefaults.buttonColors(containerColor = YaluNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Log In / Register", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Logged-in Header
                val user = (profileState as? NetworkResult.Success)?.data?.user ?: viewModel.getCurrentLocalUser()
                val userName = user?.displayName ?: "User"

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = user?.avatar ?: "https://www.yalu.in/pwa/icons/icon-192x192.png",
                                contentDescription = userName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (user?.isVerified == true) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = YaluVerifiedBadge,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                if (!user?.phone.isNullOrBlank()) {
                                    Text(
                                        text = user?.phone.orEmpty(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (!user?.email.isNullOrBlank()) {
                                    Text(
                                        text = user?.email.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(title = "My Ads", count = user?.activeAdsCount ?: 0)
                            ProfileStatItem(title = "Favorites", count = user?.favoritesCount ?: 0)
                            ProfileStatItem(title = "Member Since", label = user?.memberSince.orEmpty().ifBlank { "2024" })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Menu Items
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    if (isLoggedIn) {
                        ProfileMenuItem(
                            icon = Icons.Default.ListAlt,
                            title = "My Listings",
                            subtitle = "Manage active and pending ads",
                            onClick = onNavigateToMyAds
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }

                    ProfileMenuItem(
                        icon = Icons.Default.FavoriteBorder,
                        title = "Saved Favorites",
                        subtitle = "View bookmarked listings",
                        onClick = onNavigateToFavorites
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    ProfileMenuItem(
                        icon = Icons.Default.HelpOutline,
                        title = "Help & Support",
                        subtitle = "FAQs and 24/7 customer assistance",
                        onClick = {}
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    ProfileMenuItem(
                        icon = Icons.Default.Security,
                        title = "Safety & Community Guidelines",
                        subtitle = "Tips for secure transactions",
                        onClick = {}
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "About Yalu India",
                        subtitle = "Version 1.0.0 (Production)",
                        onClick = {}
                    )

                    if (isLoggedIn) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        ProfileMenuItem(
                            icon = Icons.Default.Logout,
                            title = "Sign Out",
                            subtitle = "Log out from this device",
                            tint = YaluRed,
                            onClick = { showLogoutDialog = true }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ProfileStatItem(title: String, count: Int? = null, label: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label ?: count?.toString() ?: "0",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = YaluNavy
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color = YaluNavy,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (tint == YaluRed) YaluRed else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
