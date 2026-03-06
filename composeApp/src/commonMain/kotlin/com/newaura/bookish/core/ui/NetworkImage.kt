package com.newaura.bookish.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage
import com.newaura.bookish.core.util.AppLogger

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeHolder: @Composable () -> Unit = {},
    contentScale: ContentScale = ContentScale.Crop
) {

    if(url.isEmpty()) {
        Box(
            modifier = modifier.background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            placeHolder.invoke()
        }
        return
    }

    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        onError = ({ error ->
           AppLogger.e(error.result.throwable)
        }),
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        error = { error ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                placeHolder()
            }
        },
    )
}