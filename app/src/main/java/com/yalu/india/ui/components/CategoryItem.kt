package com.yalu.india.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yalu.india.network.models.CategoryDto
import com.yalu.india.ui.theme.YaluNavy
import com.yalu.india.ui.theme.YaluNavyContainer

@Composable
fun CategoryCircleItem(
    category: CategoryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val key = category.slug ?: category.title.orEmpty()
    val icon = getCategoryIcon(key)

    Column(
        modifier = modifier
            .width(76.dp)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = YaluNavyContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = category.displayTitle,
                    tint = YaluNavy,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = category.displayTitle,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun CategoryCardItem(
    category: CategoryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val key = category.slug ?: category.title.orEmpty()
    val icon = getCategoryIcon(key)
    val subs = category.safeSubcategories

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = YaluNavyContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = category.displayTitle,
                        tint = YaluNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (subs.isNotEmpty()) {
                    Text(
                        text = "${subs.size} Subcategories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getCategoryIcon(key: String): ImageVector {
    val lower = key.lowercase()
    return when {
        lower.contains("mobile") || lower.contains("phone") -> Icons.Default.PhoneAndroid
        lower.contains("vehicle") || lower.contains("car") || lower.contains("bike") -> Icons.Default.DirectionsCar
        lower.contains("electronic") || lower.contains("laptop") || lower.contains("tv") -> Icons.Default.Laptop
        lower.contains("real estate") || lower.contains("property") || lower.contains("house") || lower.contains("rent") -> Icons.Default.Home
        lower.contains("job") || lower.contains("service") || lower.contains("work") -> Icons.Default.Work
        lower.contains("fashion") || lower.contains("beauty") || lower.contains("cloth") -> Icons.Default.ShoppingBag
        lower.contains("furniture") -> Icons.Default.Chair
        lower.contains("pet") || lower.contains("animal") -> Icons.Default.Pets
        else -> Icons.Default.Category
    }
}
