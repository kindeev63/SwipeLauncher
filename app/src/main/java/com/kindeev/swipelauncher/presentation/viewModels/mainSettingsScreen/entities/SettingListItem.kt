package com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen.entities

sealed class SettingListItem {
    data class Header(val header: String): SettingListItem()
    data class Category(
        val title: String,
        val description: String? = null,
        val iconUnicode: String,
        val category: SettingCategory
    ): SettingListItem()
}