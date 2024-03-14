package com.kindeev.swipelauncher.data.dialogTabs

import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage

data class OtherAction(val type: CircleMenuActionTypes, val nameResourceId: Int, val image: DefaultImage)
