package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage

data class CircleMenuItem(
    val action: CircleMenuAction,
    val image: CircleMenuImage
)