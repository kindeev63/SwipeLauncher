package com.kindeev.swipelauncher.presentation.uiElements.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.data.DefaultImageWithName
import com.kindeev.swipelauncher.data.DefaultImages

@Composable
fun PickDefaultImageDialog(
    pickedId: Int,
    onPick: (id: Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenWidth.dp - 20.dp)
                .height((screenWidth.dp - 20.dp) * 3 / 2),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive((screenWidth / 3).dp)
            ) {
                items(items = DefaultImages.images) { item: DefaultImageWithName ->
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .background(if (item.defaultImage.id == pickedId) Color.Gray.copy(alpha = 0.5f) else Color.Transparent)
                            .clickable { onPick(item.defaultImage.id) },
                        painter = painterResource(
                            id = item.defaultImage.id
                        ),
                        contentDescription = null
                    )
                }
            }
        }

    }
}