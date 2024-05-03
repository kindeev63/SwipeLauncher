package com.kindeev.swipelauncher.presentation.entities

import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import java.io.Serializable

data class ImageType(val name: String, val imageResId: Int, val type: CircleMenuImageTypes): Serializable
