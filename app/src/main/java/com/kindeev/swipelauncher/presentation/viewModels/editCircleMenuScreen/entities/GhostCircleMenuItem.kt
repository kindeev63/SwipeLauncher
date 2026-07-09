package com.kindeev.swipelauncher.presentation.viewModels.editCircleMenuScreen.entities

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage

data class GhostCircleMenuItem(
    val index: Int?,
    val image: CircleMenuImage,
    val offset: Offset,
    val firstOffset: Offset,
    val size: Float
)
