package com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.entities

sealed class ActionItemState {
    object Add: ActionItemState()
    object Delete: ActionItemState()
    object DeleteActive: ActionItemState()
}
