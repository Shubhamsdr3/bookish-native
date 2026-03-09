package com.newaura.bookish.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.newaura.bookish.core.data.ActivityLevel

fun activityColor(level: ActivityLevel): Color = when (level) {
    ActivityLevel.NONE      -> Color(0xFFEEEEEE)
    ActivityLevel.LOW       -> Color(0xFFC8E6C9)
    ActivityLevel.MEDIUM    -> Color(0xFF66BB6A)
    ActivityLevel.HIGH      -> Color(0xFF2E7D5E)
    ActivityLevel.VERY_HIGH -> Color(0xFF1B4D3E)
}

@Composable
fun HeatmapDot(
    level: ActivityLevel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(activityColor(level))
    )
}
