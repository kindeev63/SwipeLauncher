package com.kindeev.swipelauncher.presentation.ui.elements.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.Constants

@Composable
fun SwitchSettingItem(
    text: String,
    value: Boolean,
    first: Boolean = true,
    last: Boolean = true,
    onChangeValue: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Constants.minScreenLength.dp / 5)
            .clip(
                RoundedCornerShape(
                    topStart = if (first) 7.dp else 0.dp,
                    topEnd = if (first) 7.dp else 0.dp,
                    bottomStart = if (last) 7.dp else 0.dp,
                    bottomEnd = if (last) 7.dp else 0.dp,
                )
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = Constants.settingsTextSize
        )
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = value,
            onCheckedChange = onChangeValue
        )
    }
}