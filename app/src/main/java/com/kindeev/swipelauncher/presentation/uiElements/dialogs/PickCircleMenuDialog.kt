package com.kindeev.swipelauncher.presentation.uiElements.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.presentation.uiElements.MiniCircleMenuItem

@Composable
fun PickCircleMenuDialog(
    pickedId: Int?,
    onPick: (id: Int) -> Unit,
    allCircleMenus: List<CircleMenu>,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = androidx.compose.ui.Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .heightIn(max = (screenConfiguration.screenHeightDp / 3 * 2).dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                items(
                    items = allCircleMenus
                ) { circleMenu ->
                    MiniCircleMenuItem(
                        picked = circleMenu.id == pickedId,
                        size = (Integer.min(
                            LocalConfiguration.current.screenWidthDp,
                            LocalConfiguration.current.screenHeightDp
                        ) - 20f) / 3,
                        circleMenu = circleMenu) {
                        onPick(circleMenu.id)
                    }
                }
            }
        }
    }
}