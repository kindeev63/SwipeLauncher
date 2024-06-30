package com.kindeev.swipelauncher.domain.entities.imageTypes

import java.io.Serializable

data class ImageType(
    val name: String,
    val imageResId: Int,
    val type: AllImageTypes
): Serializable
