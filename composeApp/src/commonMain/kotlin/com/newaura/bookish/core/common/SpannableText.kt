package com.newaura.bookish.core.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class TextSpan(
    val text: String,
    val fontSize: TextUnit = 14.sp,
    val fontWeight: FontWeight = FontWeight.Normal,
    val color: Color = Color.Black,
    val fontStyle: FontStyle = FontStyle.Normal
)

fun buildSpannableString(vararg spans: TextSpan): AnnotatedString {
    return buildAnnotatedString {
        spans.forEach { span ->
            withStyle(
                style = SpanStyle(
                    fontSize = span.fontSize,
                    fontWeight = span.fontWeight,
                    color = span.color,
                    fontStyle = span.fontStyle
                )
            ) {
                append(span.text)
            }
        }
    }
}

@Composable
fun SpannableText(
    vararg spans: TextSpan,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val annotatedString = buildSpannableString(*spans)
    Text(text = annotatedString, modifier = modifier)
}

