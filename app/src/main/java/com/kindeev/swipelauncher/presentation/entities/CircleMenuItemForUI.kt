package com.kindeev.swipelauncher.presentation.entities

import androidx.compose.ui.graphics.ImageBitmap
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage

data class CircleMenuItemForUI(
    val imageBitmap: ImageBitmap,
    val image: CircleMenuImage,
    val action: CircleMenuAction
)
