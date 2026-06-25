package com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kindeev.swipelauncher.data.userImages.getCoilModel
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.GhostCircleMenuItem

@Composable
fun GhostCircleMenuItemUI(
    item: GhostCircleMenuItem
) {
    val context = LocalContext.current
    AsyncImage(
        model = item.image.getCoilModel(context),
        modifier = Modifier
            .offset(
                x = item.offset.x.dp - item.size.dp / 2,
                y = item.offset.y.dp - item.size.dp / 2
            )
            .size(item.size.dp),
        contentDescription = null
    )
}