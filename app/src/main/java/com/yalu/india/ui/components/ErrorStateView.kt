package com.yalu.india.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yalu.india.ui.theme.YaluNavy

@Composable
fun ErrorStateView(
    message: String?,
    onRetry: (() -> Unit)? = null,
    icon: ImageVector = Icons.Outlined.CloudOff,
    modifier: Modifier = Modifier
) {
    val displayMessage = message.orEmpty().ifBlank { "Unable to connect to Yalu servers. Please try again." }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = YaluNavy)
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    title: String? = "No listings found",
    subtitle: String? = "Try adjusting your filters or search keywords.",
    icon: ImageVector = Icons.Outlined.SearchOff,
    modifier: Modifier = Modifier
) {
    val displayTitle = title.orEmpty().ifBlank { "No listings found" }
    val displaySubtitle = subtitle.orEmpty().ifBlank { "Try adjusting your filters or search keywords." }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayTitle,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = displaySubtitle,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
