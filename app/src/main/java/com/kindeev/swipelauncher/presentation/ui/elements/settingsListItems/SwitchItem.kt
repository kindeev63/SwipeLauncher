package com.kindeev.swipelauncher.presentation.ui.elements.settingsListItems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.presentation.ui.elements.MaterialIcon

@Composable
fun SwitchItem(title: String, description: String? = null, iconUnicode: String, chacked: Boolean, onChackedChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        supportingContent = description?.let {
            {
                Text(text = description)
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
                        unicode = iconUnicode,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        trailingContent = {
            Switch(
                checked = chacked,
                onCheckedChange = onChackedChange
            )
        }
    )
}
