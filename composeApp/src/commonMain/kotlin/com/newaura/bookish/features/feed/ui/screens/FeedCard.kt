package com.newaura.bookish.features.feed.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newaura.bookish.core.common.TextViewBody
import com.newaura.bookish.core.common.SpannableText
import com.newaura.bookish.core.common.TextSpan
import com.newaura.bookish.core.common.TextViewMedium
import com.newaura.bookish.core.ui.NetworkImage
import com.newaura.bookish.model.FeedData
import com.newaura.bookish.model.FeedPost
import com.newaura.bookish.model.PostType

@Composable
fun FeedCard(
    feedData: FeedData,
    onClick: () -> Unit,
    onBookNameClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        colors = CardDefaults.cardColors().copy(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                NetworkImage(
                    url = feedData.user?.profileIcon ?: "",
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeHolder = {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile placeholder",
                        )
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    SpannableText(
                        TextSpan(
                            text = feedData.user?.name ?: "Guest user",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        ),
                        TextSpan(
                            text = getPostTypeLabel(feedData),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (feedData.post?.bookName?.isNotBlank() == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        onBookNameClick(feedData.post.bookName)
                                    },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            TextViewMedium(
                                text = feedData.post.bookName,
                                fontSize = 14.sp,
                                color = Color(0xFF007A55),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    TextViewBody(
                        text = feedData.getFormattedCreatedAt(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            PostCaption(feedData.post)
            ImageCarousel(feedData.post?.images)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                ActionButton(
                    label = "${feedData.like?.count ?: 0}",
                    icon = {
                        Icon(
                            Icons.Outlined.ThumbUp,
                            contentDescription = "",
                            tint = Color.Gray.copy(alpha = 0.4f)
                        )
                    },
                    onClick = {

                    }
                )
                ActionButton(
                    label = "Comment",
                    icon = {
                        Icon(
                            Icons.Outlined.Comment,
                            contentDescription = "",
                            tint = Color.Gray.copy(alpha = 0.4f)
                        )
                    },
                    onClick = {

                    }
                )
                ActionButton(
                    label = "Share",
                    icon = {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "",
                            tint = Color.Gray.copy(alpha = 0.4f)
                        )
                    },
                    onClick = {

                    }
                )
            }
        }
    }
}

private fun getPostTypeLabel(feedData: FeedData): String {
    return when (feedData.post?.postType) {
        PostType.QUOTE -> " shared quote on"
        PostType.REVIEW -> " shared review on"
        else -> " shared though on"
    }
}

@Composable
fun ImageCarousel(images: List<String>?) {
    if (images.isNullOrEmpty()) return

    val pagerState = remember { PagerState(pageCount = { images.size }) }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) { index ->
            NetworkImage(
                url = images[index],
                contentDescription = "Post image ${index + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { index ->
                    Surface(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 6.dp else 4.dp)
                            .clip(CircleShape),
                        color = if (pagerState.currentPage == index)
                            Color.Black else Color.Gray.copy(alpha = 0.3f)
                    ) {}
                    if (index < images.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PostCaption(post: FeedPost?) {
    post?.caption?.let { caption ->
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (post.postType == PostType.QUOTE) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.Cyan)
                )
                Spacer(modifier = Modifier.width(12.dp))
                SpannableText(
                    TextSpan(
                        text = caption,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.8f),
                        fontStyle = FontStyle.Italic
                    )
                )
            } else {
                TextViewBody(caption)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        icon()
        Spacer(modifier = Modifier.width(6.dp))
        TextViewBody(label, color = Color.LightGray)
    }
}