package com.kindeev.swipelauncher.domain.circleMenuActions

import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.NoneAction

data class CircleMenuAction(val type: CircleMenuActionTypes = CircleMenuActionTypes.NoneAction, val data: Any = NoneAction)