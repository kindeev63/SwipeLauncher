package com.kindeev.swipelauncher.domain.entities.dialogTabs

import com.kindeev.swipelauncher.R

sealed class ActionDialogTabs(val nameResourceId: Int) {
    object OpenAppTab: ActionDialogTabs(nameResourceId = R.string.open_app_tab)
    object OpenCircleMenuTab: ActionDialogTabs(nameResourceId = R.string.open_circle_menu_tab)
    object OtherTab: ActionDialogTabs(nameResourceId = R.string.other)
}
