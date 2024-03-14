package com.kindeev.swipelauncher.domain.entities

import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage

data class CircleMenuItem(
    val action: CircleMenuAction,
    val image: CircleMenuImage
)