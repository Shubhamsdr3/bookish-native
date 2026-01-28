package com.newaura.bookish.core.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun TextViewBold(text: String, size: Int, color: Color) {
    Text(text, fontSize = size.sp, fontWeight = FontWeight.Bold, color = color)
}

@Composable
fun TextViewSemiBold(text: String, fontSize: TextUnit, color: Color) {
    Text(text, fontSize = fontSize, fontWeight = FontWeight.Bold, color = color)
}

@Composable
fun TextViewMedium(text: String, fontSize: TextUnit = 16.sp, color: Color = Color.Black) {
    Text(text, fontSize = fontSize, fontWeight = FontWeight.Medium, color = color)
}

@Composable
fun TextViewBody(text: String, fontSize: TextUnit = 14.sp, color: Color = Color.Black) {
    Text(text, fontSize = fontSize, fontWeight = FontWeight.Normal, color = color)
}

@Composable
fun TextViewLight(text: String, fontSize: TextUnit = 12.sp, color: Color = Color.Black) {
    Text(text, fontSize = fontSize, fontWeight = FontWeight.Light, color = color)
}