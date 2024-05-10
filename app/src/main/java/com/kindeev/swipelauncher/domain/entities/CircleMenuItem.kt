package com.kindeev.swipelauncher.domain.entities

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import java.io.Serializable

data class CircleMenuItem(
    val offset: Offset,
    val image: CircleMenuImage,
    val action: CircleMenuAction,
): Serializable