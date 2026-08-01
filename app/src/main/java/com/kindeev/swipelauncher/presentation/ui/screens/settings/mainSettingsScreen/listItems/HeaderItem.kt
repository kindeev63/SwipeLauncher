package com.kindeev.swipelauncher.presentation.ui.screens.settings.mainSettingsScreen.listItems

import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen.entities.SettingListItem

@Composable
fun HeaderItem(header: SettingListItem.Header) {
    ListItem(
        headlineContent = {
            Text(
                text = header.header,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}
