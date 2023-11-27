package com.kindeev.swipelauncher.domain.circleMenuImages

import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.NoneImage

data class CircleMenuImage(val type: CircleMenuImageTypes = CircleMenuImageTypes.NoneImage, val data: Any = NoneImage)