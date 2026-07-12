package com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R

@Composable
fun AddCircleMenuItemUI(
    size: Float,
    itemSize: Float,
) {
    Image(
        modifier = Modifier
            .offset(
                x = size.dp / 2 - itemSize.dp / 2,
                y = size.dp / 2 - itemSize.dp / 2
            )
            .size(itemSize.dp),
        painter = painterResource(R.drawable.ic_add_circle),
        colorFilter = ColorFilter.tint(Color.Black),
        contentDescription = null
    )
}