package com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen

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
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.ActionItemData
import com.kindeev.swipelauncher.R

@Composable
fun DeleteCircleMenuItemUI(
    size: Float,
    actionItemData: ActionItemData
) {
    if (actionItemData.elementOnTop) {
        Image(
            modifier = Modifier
                .offset(
                    x = size.dp / 2 - actionItemData.size.dp / 2,
                    y = size.dp / 2 - actionItemData.size.dp / 2
                )
                .size(actionItemData.size.dp)
                .clip(CircleShape)
                .background(Color.Companion.Red),
            painter = painterResource(R.drawable.ic_delete),
            colorFilter = ColorFilter.Companion.tint(Color.Companion.White),
            contentDescription = null
        )
    } else {
        Image(
            modifier = Modifier
                .offset(
                    x = size.dp / 2 - actionItemData.size.dp / 2,
                    y = size.dp / 2 - actionItemData.size.dp / 2
                )
                .size(actionItemData.size.dp),
            painter = painterResource(R.drawable.ic_delete),
            colorFilter = ColorFilter.Companion.tint(Color.Companion.Black),
            contentDescription = null
        )
    }
}