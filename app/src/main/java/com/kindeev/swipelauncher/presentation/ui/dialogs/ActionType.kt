package com.kindeev.swipelauncher.presentation.ui.dialogs

import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import java.io.Serializable

data class ActionType(val nameResId: Int, val imageResId: Int, val type: CircleMenuActionTypes): Serializable