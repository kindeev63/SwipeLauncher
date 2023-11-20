package com.kindeev.swipelauncher.domain.circleMenuImages

import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.NoneImage

data class CircleMenuImage(val type: CircleMenuImageTypes, val data: Any = NoneImage)