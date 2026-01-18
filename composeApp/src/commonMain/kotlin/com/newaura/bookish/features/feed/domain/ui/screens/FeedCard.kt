package com.newaura.bookish.features.feed.domain.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation. layout.*
import androidx.compose. foundation.shape.CircleShape
import androidx.compose. material3.*
import androidx.compose.runtime. Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose. ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx. compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.newaura.bookish.model.FeedData

@Composable
fun FeedCard(
    feedData: FeedData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            . fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = feedData. user?.profileIcon,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = feedData.user?. name ?: "Unknown User",
                        style = MaterialTheme. typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${feedData.user?.connections ?: 0} connections",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier. height(12.dp))

            // Caption
            feedData.post?.caption?.let { caption ->
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier. height(12.dp))
            }

            // Images
            feedData.post?.images?.firstOrNull()?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale. Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Actions Row
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    count = feedData.post?.like?.count ?: 0,
                    label = "Like"
                )
                ActionButton(
                    count = feedData. post?.comment?.count ?: 0,
                    label = "Comment"
                )
                ActionButton(
                    count = feedData.post?.share?.count ?: 0,
                    label = "Share"
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    count: Int,
    label: String
) {
    TextButton(onClick = { /* Handle action */ }) {
        Text("$count $label")
    }
}