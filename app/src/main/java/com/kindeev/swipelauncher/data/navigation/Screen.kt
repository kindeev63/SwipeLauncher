package com.kindeev.swipelauncher.data.navigation

sealed class Screen(
    val route: String
) {

    object SwipeScreenObject: Screen(ROUTE_SWIPE_SCREEN)
    object SettingsScreenObject: Screen(ROUTE_SETTINGS_SCREEN)

    object MainSettingsScreenObject: Screen(ROUTE_MAIN_SETTINGS_SCREEN)

    object AllCircleMenusScreenObject: Screen(ROUTE_ALL_CIRCLE_MENUS_SCREEN)
    object EditCircleMenuScreenObject: Screen(ROUTE_EDIT_CIRCLE_MENU_SCREEN) {
        private const val ROUTE_FOR_ARGS = "edit_circle_menu_screen"

        fun getRouteWithArgs(circleMenuId: Int?) = "$ROUTE_FOR_ARGS/$circleMenuId"
    }

    private companion object {
        const val ROUTE_SWIPE_SCREEN = "swipe_screen"
        const val ROUTE_SETTINGS_SCREEN = "settings_screen"

        const val ROUTE_MAIN_SETTINGS_SCREEN = "main_settings_screen"
        const val ROUTE_ALL_CIRCLE_MENUS_SCREEN = "all_circle_menus_screen"
        const val ROUTE_EDIT_CIRCLE_MENU_SCREEN = "edit_circle_menu_screen/{circleMenuId}"
    }
}
