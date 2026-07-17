package com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.entities

sealed class ActionItemState {
    object Add: ActionItemState()
    object Delete: ActionItemState()
    object DeleteActive: ActionItemState()
}
