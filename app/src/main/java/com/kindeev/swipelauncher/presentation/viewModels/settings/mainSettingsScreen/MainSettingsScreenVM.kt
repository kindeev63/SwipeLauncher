package com.kindeev.swipelauncher.presentation.viewModels.settings.mainSettingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.utils.showLauncherSelection
import com.kindeev.swipelauncher.presentation.entities.SettingsListItem
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.viewModels.settings.mainSettingsScreen.entities.SettingCategory
import com.knomster.navigation_component.NavigationComponent

class MainSettingsScreenVM(
    private val navigationComponent: NavigationComponent<SettingsActivityNav>,
    context: Context
): ViewModel() {

    private val pickLauncher = context::showLauncherSelection
    val settingCategories = listOf(
        SettingsListItem.Header(
            header = context.getString(R.string.general_header)
        ),
        SettingsListItem.Category(
            id = SettingCategory.CircleMenu,
            title = context.getString(R.string.circle_menu_category_title),
            description = context.getString(R.string.circle_menu_category_description),
            iconUnicode = "\uebd5",
        ),
        SettingsListItem.Category(
            id = SettingCategory.LauncherScreen,
            title = context.getString(R.string.main_screen_category_title),
            description = context.getString(R.string.main_screen_category_description),
            iconUnicode = "\ue88a",
        ),
        SettingsListItem.Category(
            id = SettingCategory.AppList,
            title = context.getString(R.string.app_list_category_title),
            description = context.getString(R.string.app_list_category_description),
            iconUnicode = "\ue5c3",
        ),
        SettingsListItem.Header(
            header = context.getString(R.string.additionally_header)
        ),
        SettingsListItem.Category(
            id = SettingCategory.PickLauncher,
            title = context.getString(R.string.pick_launcher_category_title),
            iconUnicode = "\ueb9b",
        ),
        SettingsListItem.Category(
            id = SettingCategory.Tutorial,
            title = context.getString(R.string.tutorial_category_title),
            description = context.getString(R.string.tutorial_category_description),
            iconUnicode = "\uea19",
        ),
        SettingsListItem.Category(
            id = SettingCategory.Additional,
            title = context.getString(R.string.additional_category_title),
            description = context.getString(R.string.additional_category_description),
            iconUnicode = "\ue87b"
        )
    )

    fun clickOnCategory(category: SettingCategory) {
        when (category) {
            SettingCategory.CircleMenu -> {
                navigationComponent.addToBackStack(SettingsActivityNav.CircleMenus)
            }
            SettingCategory.LauncherScreen -> {
                navigationComponent.addToBackStack(SettingsActivityNav.Launcher)
            }
            SettingCategory.AppList -> {
                navigationComponent.addToBackStack(SettingsActivityNav.AppList)
            }
            SettingCategory.PickLauncher -> {
                pickLauncher()
            }
            SettingCategory.Tutorial -> {
                navigationComponent.addToBackStack(SettingsActivityNav.Tutorial)
            }

            SettingCategory.Additional -> {
                navigationComponent.addToBackStack(SettingsActivityNav.Additional)
            }
        }
    }
}