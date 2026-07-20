package com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.entities

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction

data class CircleMenuItemToDrawVM(
    val item: CircleMenuItemToDraw,
    val action: CircleMenuAction
)