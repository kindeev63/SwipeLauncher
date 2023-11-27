package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import java.io.Serializable

data class MenuImages(
    val upImage: CircleMenuImage,
    val downImage: CircleMenuImage,
    val rightImage: CircleMenuImage,
    val leftImage: CircleMenuImage,
): Serializable
