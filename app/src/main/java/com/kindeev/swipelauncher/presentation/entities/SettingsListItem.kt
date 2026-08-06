package com.kindeev.swipelauncher.presentation.entities


sealed class SettingsListItem<T> {
    data class Category<T>(
        val id: T,
        val title: String,
        val description: String? = null,
        val iconUnicode: String,
    ): SettingsListItem<T>()

    data class Switch<T>(
        val id: T,
        val title: String,
        val description: String? = null,
        val iconUnicode: String,
        val checked: Boolean
    ): SettingsListItem<T>()

    data class SwitchWithAction<T>(
        val id: T,
        val title: String,
        val description: String? = null,
        val iconUnicode: String,
        val checked: Boolean,
        val actionItemData: ActionItemData?
    ): SettingsListItem<T>()

    data class Header<T>(
        val header: String
    ): SettingsListItem<T>()
}