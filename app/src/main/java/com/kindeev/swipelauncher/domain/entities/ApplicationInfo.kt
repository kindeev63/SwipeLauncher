package com.kindeev.swipelauncher.domain.entities

import androidx.compose.ui.graphics.ImageBitmap
import java.io.Serializable

data class ApplicationInfo(
    val title: String,
    val icon: ImageBitmap,
    val packageName: String
): Serializable
