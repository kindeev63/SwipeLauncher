package com.kindeev.swipelauncher.presentation.entities

import androidx.compose.ui.graphics.ImageBitmap
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction

data class CircleMenuItemForUI(
    val image: ImageBitmap,
    val action: CircleMenuAction
)
