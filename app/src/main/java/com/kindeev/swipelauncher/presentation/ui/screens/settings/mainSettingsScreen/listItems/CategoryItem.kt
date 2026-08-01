package com.kindeev.swipelauncher.presentation.ui.screens.settings.mainSettingsScreen.listItems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.presentation.ui.elements.MaterialIcon
import com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen.entities.SettingListItem

@Composable
fun CategoryItem(category: SettingListItem.Category, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(text = category.title)
        },
        supportingContent = category.description?.let {
            {
                Text(text = category.description)
            }
        },
        leadingContent = {
            Surface(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        unicode = category.iconUnicode,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}
