package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage

data class OtherAction(val type: CircleMenuActionTypes, val nameResourceId: Int, val image: DefaultImage)
