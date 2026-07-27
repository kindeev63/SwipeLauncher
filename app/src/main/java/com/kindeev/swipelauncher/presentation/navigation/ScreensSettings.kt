package com.kindeev.swipelauncher.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreensSettings {
    @Serializable
    object MainSettingsScreenObject: ScreensSettings()

    @Serializable
    object AllCircleMenusScreenObject: ScreensSettings()

    @Serializable
    object TutorialScreenObject: ScreensSettings()

    @Serializable
    data class EditCircleMenuScreenObject(val circleMenuId: Int? = null): ScreensSettings()

    @Serializable
    object ActionDialog: ScreensSettings()

    @Serializable
    object ImageDialog: ScreensSettings()
}
