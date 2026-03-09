package com.newaura.bookish.core.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    radius: Int = 16
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4CAF7D), // Green (left)
            Color(0xFF2E8B7A)  // Teal (right)
        )
    )
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(radius.dp),
                ambientColor = Color(0xFF2E8B7A).copy(alpha = 0.4f),
                spotColor = Color(0xFF2E8B7A).copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(radius.dp))
            .background(brush = gradientBrush)
            .clickable { onClick() }
            .padding(horizontal = 36.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        TextViewBody(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}