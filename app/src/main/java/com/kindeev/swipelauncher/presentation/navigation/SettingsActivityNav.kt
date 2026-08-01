package com.kindeev.swipelauncher.presentation.navigation

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsActivityNav {

    @Serializable
    object Main: SettingsActivityNav()

    @Serializable
    object CircleMenus: SettingsActivityNav()

    @Serializable
    object Tutorial: SettingsActivityNav()

    @Serializable
    data class ActionDialog(val onPick: (CircleMenuAction) -> Unit): SettingsActivityNav()

    @Serializable
    data class ImageDialog(val onPick: (CircleMenuImage) -> Unit): SettingsActivityNav()

    @Serializable
    data class EditCircleMenu(val circleMenuId: Int?): SettingsActivityNav()
}