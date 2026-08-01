package com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R

@Composable
fun DeleteCircleMenuItemUI(
    menuSize: Float,
    size: Float,
    elementOnTop: Boolean,
) {
    if (elementOnTop) {
        Image(
            modifier = Modifier
                .offset(
                    x = menuSize.dp / 2 - size.dp / 2,
                    y = menuSize.dp / 2 - size.dp / 2
                )
                .size(size.dp)
                .clip(CircleShape)
                .background(Color.Red),
            painter = painterResource(R.drawable.ic_delete),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null
        )
    } else {
        Image(
            modifier = Modifier
                .offset(
                    x = menuSize.dp / 2 - size.dp / 2,
                    y = menuSize.dp / 2 - size.dp / 2
                )
                .size(size.dp),
            painter = painterResource(R.drawable.ic_delete),
            colorFilter = ColorFilter.tint(Color.Black),
            contentDescription = null
        )
    }
}