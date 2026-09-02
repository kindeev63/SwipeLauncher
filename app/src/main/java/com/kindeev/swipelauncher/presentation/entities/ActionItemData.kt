package com.kindeev.swipelauncher.presentation.entities

import android.net.Uri

sealed class ActionItemData {
    data class Call(val phoneNumber: String): ActionItemData()
    data class Dial(val phoneNumber: String): ActionItemData()
    data object FlashlightOff: ActionItemData()
    data object FlashlightOn: ActionItemData()
    data object ChangeFlashlightCondition: ActionItemData()
    data object OpenSettings: ActionItemData()
    data class OpenApp(val title: String, val imageUri: Uri): ActionItemData()
    data class OpenCircleMenu(val title: String, val items: List<CircleMenuItemToDraw>, val itemSize: Float): ActionItemData()
    data class OpenUrl(val url: String): ActionItemData()

    data object Empty: ActionItemData()
}