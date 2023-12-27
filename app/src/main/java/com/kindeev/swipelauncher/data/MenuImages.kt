package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import java.io.Serializable

data class MenuImages(
    var upImage: CircleMenuImage,
    var downImage: CircleMenuImage,
    var rightImage: CircleMenuImage,
    var leftImage: CircleMenuImage,
): Serializable
