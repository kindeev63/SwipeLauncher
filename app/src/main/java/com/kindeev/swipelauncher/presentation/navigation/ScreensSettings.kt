package com.kindeev.swipelauncher.presentation.navigation

sealed class ScreensSettings(
    val route: String
) {

    object MainSettingsScreenObject: ScreensSettings(ROUTE_MAIN_SETTINGS_SCREEN)

    object AllCircleMenusScreenObject: ScreensSettings(ROUTE_ALL_CIRCLE_MENUS_SCREEN)

    object HiddenAppsScreenObject: ScreensSettings(ROUTE_HIDDEN_APPS_SCREEN)
    object EditCircleMenuScreenObject: ScreensSettings(ROUTE_EDIT_CIRCLE_MENU_SCREEN) {
        private const val ROUTE_FOR_ARGS = "edit_circle_menu_screen"

        fun getRouteWithArgs(circleMenuId: Int?) = "$ROUTE_FOR_ARGS/$circleMenuId"
    }

    private companion object {
        const val ROUTE_MAIN_SETTINGS_SCREEN = "main_settings_screen"
        const val ROUTE_ALL_CIRCLE_MENUS_SCREEN = "all_circle_menus_screen"
        const val ROUTE_HIDDEN_APPS_SCREEN = "hidden_apps_screen"
        const val ROUTE_EDIT_CIRCLE_MENU_SCREEN = "edit_circle_menu_screen/{circleMenuId}"
    }
}
