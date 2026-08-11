package com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R

@Composable
fun AddCircleMenuItemUI(
    menuSize: Float,
    size: Float,
) {
    Image(
        modifier = Modifier
            .offset(
                x = menuSize.dp / 2 - size.dp / 2,
                y = menuSize.dp / 2 - size.dp / 2
            )
            .size(size.dp),
        painter = painterResource(R.drawable.ic_add_circle),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
        contentDescription = null
    )
}