package com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen.entities.SettingCategory
import com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen.entities.SettingListItem

class MainSettingsScreen2VM(
    context: Context
): ViewModel() {
    val settingCategories = listOf(
        SettingListItem.Header(
            header = context.getString(R.string.general_header)
        ),
        SettingListItem.Category(
            title = context.getString(R.string.circle_menu_category_title),
            description = context.getString(R.string.circle_menu_category_description),
            iconUnicode = "\uebd5",
            category = SettingCategory.CircleMenu
        ),
        SettingListItem.Category(
            title = context.getString(R.string.app_list_category_title),
            description = context.getString(R.string.app_list_category_description),
            iconUnicode = "\ue5c3",
            category = SettingCategory.AppList
        ),
        SettingListItem.Header(
            header = context.getString(R.string.additionally_header)
        ),
        SettingListItem.Category(
            title = context.getString(R.string.pick_launcher_category_title),
            iconUnicode = "\ueb9b",
            category = SettingCategory.PickLauncher
        ),
        SettingListItem.Category(
            title = context.getString(R.string.tutorial_category_title),
            description = context.getString(R.string.tutorial_category_description),
            iconUnicode = "\uea19",
            category = SettingCategory.Tutorial
        ),
    )

    fun clickOnCategory(category: SettingCategory) {

    }
}