package com.kindeev.swipelauncher.presentation.interfaces

import androidx.compose.ui.graphics.ImageBitmap
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import kotlinx.coroutines.flow.StateFlow

interface CircleMenuImageToImageBitmap {
    val mapper: StateFlow<Map<CircleMenuImage, ImageBitmap>>
}