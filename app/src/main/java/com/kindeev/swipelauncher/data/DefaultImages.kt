package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage

data class DefaultImageWithName(val name: String, val defaultImage: DefaultImage)
object DefaultImages {
    val images = listOf(
        DefaultImageWithName(name = "settings", defaultImage = DefaultImage(R.drawable.ic_settings)),
        DefaultImageWithName(name = "up_arrow", defaultImage = DefaultImage(R.drawable.ic_up_arrow)),
        DefaultImageWithName(name = "down_arrow", defaultImage = DefaultImage(R.drawable.ic_down_arrow)),
        DefaultImageWithName(name = "right_arrow", defaultImage = DefaultImage(R.drawable.ic_right_arrow)),
        DefaultImageWithName(name = "left_arrow", defaultImage = DefaultImage(R.drawable.ic_left_arrow)),
    )
}