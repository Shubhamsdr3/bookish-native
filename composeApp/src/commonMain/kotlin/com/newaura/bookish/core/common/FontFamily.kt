package com.newaura.bookish.core.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import bookish.composeapp.generated.resources.Res
import bookish.composeapp.generated.resources.Roboto_Bold
import bookish.composeapp.generated.resources.Roboto_Light
import bookish.composeapp.generated.resources.Roboto_Medium
import bookish.composeapp.generated.resources.Roboto_Regular
import bookish.composeapp.generated.resources.Roboto_SemiBold
import org.jetbrains.compose.resources.Font

@Composable
fun robotoFontFamily() = FontFamily(
    Font(Res.font.Roboto_Regular, FontWeight.Normal),
    Font(Res.font.Roboto_Medium, FontWeight.Medium),
    Font(Res.font.Roboto_SemiBold, FontWeight.SemiBold),
    Font(Res.font.Roboto_Bold, FontWeight.Bold),
    Font(Res.font.Roboto_Light, FontWeight.Light),
)