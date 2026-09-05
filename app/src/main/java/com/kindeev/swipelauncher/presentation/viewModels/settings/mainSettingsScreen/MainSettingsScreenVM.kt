package com.kindeev.swipelauncher.presentation.viewModels.settings.mainSettingsScreen

import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.presentation.entities.SettingsListItem
import com.kindeev.swipelauncher.presentation.interfaces.StringGetter
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.useCases.DonationUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenChannelUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenSourceCodeUseCase
import com.kindeev.swipelauncher.presentation.useCases.ShowLauncherSelectionUseCase
import com.kindeev.swipelauncher.presentation.viewModels.settings.mainSettingsScreen.entities.SettingCategory
import com.knomster.navigation_component.NavigationComponent

class MainSettingsScreenVM(
    private val navigationComponent: NavigationComponent<SettingsActivityNav>,
    private val openChannelUseCase: OpenChannelUseCase,
    private val openSourceCodeUseCase: OpenSourceCodeUseCase,
    private val donationUseCase: DonationUseCase,
    private val showLauncherSelectionUseCase: ShowLauncherSelectionUseCase,
    stringGetter: StringGetter
): ViewModel() {

    val settingCategories = listOf(
        SettingsListItem.Header(
            header = stringGetter.getString(R.string.general_header)
        ),
        SettingsListItem.Category(
            id = SettingCategory.CircleMenu,
            title = stringGetter.getString(R.string.circle_menu_category_title),
            description = stringGetter.getString(R.string.circle_menu_category_description),
            iconUnicode = "\uebd5",
        ),
        SettingsListItem.Category(
            id = SettingCategory.LauncherScreen,
            title = stringGetter.getString(R.string.main_screen_category_title),
            description = stringGetter.getString(R.string.main_screen_category_description),
            iconUnicode = "\ue88a",
        ),
        SettingsListItem.Category(
            id = SettingCategory.AppList,
            title = stringGetter.getString(R.string.app_list_category_title),
            description = stringGetter.getString(R.string.app_list_category_description),
            iconUnicode = "\ue5c3",
        ),
        SettingsListItem.Header(
            header = stringGetter.getString(R.string.additionally_header)
        ),
        SettingsListItem.Category(
            id = SettingCategory.PickLauncher,
            title = stringGetter.getString(R.string.pick_launcher_category_title),
            iconUnicode = "\ueb9b",
        ),
        SettingsListItem.Category(
            id = SettingCategory.Tutorial,
            title = stringGetter.getString(R.string.tutorial_category_title),
            description = stringGetter.getString(R.string.tutorial_category_description),
            iconUnicode = "\uea19",
        ),
        SettingsListItem.Category(
            id = SettingCategory.Additional,
            title = stringGetter.getString(R.string.additional_category_title),
            description = stringGetter.getString(R.string.additional_category_description),
            iconUnicode = "\ue87b"
        ),
        SettingsListItem.Category(
            id = SettingCategory.Channel,
            title = stringGetter.getString(R.string.channel_category_title),
            description = stringGetter.getString(R.string.chanel_category_description),
            iconUnicode = "\ue163"
        ),
        SettingsListItem.Category(
            id = SettingCategory.Code,
            title = stringGetter.getString(R.string.code_category_title),
            description = stringGetter.getString(R.string.code_category_description),
            iconUnicode = "\ue86f"
        ),
        SettingsListItem.Category(
            id = SettingCategory.Donation,
            title = stringGetter.getString(R.string.donation_category_title),
            description = stringGetter.getString(R.string.donation_category_description),
            iconUnicode = "\uea70"
        ),
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
                showLauncherSelectionUseCase.show()
            }
            SettingCategory.Tutorial -> {
                navigationComponent.addToBackStack(SettingsActivityNav.Tutorial)
            }

            SettingCategory.Additional -> {
                navigationComponent.addToBackStack(SettingsActivityNav.Additional)
            }

            SettingCategory.Channel -> {
                openChannelUseCase.open()
            }

            SettingCategory.Code -> {
                openSourceCodeUseCase.open()
            }

            SettingCategory.Donation -> {
                donationUseCase.open()
            }
        }
    }
}