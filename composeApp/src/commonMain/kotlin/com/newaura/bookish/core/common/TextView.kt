package com.newaura.bookish.core.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import extractTextFromHtml

@Composable
fun TextViewBold(text: String, fontSize: TextUnit, color: Color = Color.Black,) {
    Text(text, fontSize = fontSize, fontWeight = FontWeight.Bold, color = color)
}

@Composable
fun TextViewSemiBold(text: String, fontSize: TextUnit, color: Color = Color.Black,) {
    Text(text, fontSize = fontSize, fontWeight = FontWeight.Bold, color = color)
}

@Composable
fun TextViewMedium(
    text: String,
    fontSize: TextUnit = 16.sp,
    color: Color = Color.Black,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium,
        color = color,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextViewBody(
    text: String,
    fontSize: TextUnit = 14.sp,
    color: Color = Color.Black,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = FontWeight.Normal,
        color = color,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextViewLight(text: String, fontSize: TextUnit = 12.sp, color: Color = Color.Black) {
    Text(text, fontSize = fontSize, fontWeight = FontWeight.Light, color = color)
}

@Composable
fun HtmlTextView(htmlText: String) {
    val parsed = remember(htmlText) { extractTextFromHtml(htmlText) }
    val annotatedString = buildAnnotatedString {
        append(parsed.text)
        parsed.spans.forEach { span ->
            when (span.style) {
                HtmlStyle.ITALIC -> addStyle(
                    SpanStyle(fontStyle = FontStyle.Italic), span.start, span.end
                )

                HtmlStyle.BOLD -> addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold), span.start, span.end
                )

                HtmlStyle.BOLD_ITALIC -> addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                    span.start, span.end
                )
            }
        }
    }
    Text(text = annotatedString)
}