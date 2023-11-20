package com.kindeev.swipelauncher.domain.circleMenuActions

import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.NoneAction

data class CircleMenuAction(val type: CircleMenuActionTypes, val data: Any = NoneAction)