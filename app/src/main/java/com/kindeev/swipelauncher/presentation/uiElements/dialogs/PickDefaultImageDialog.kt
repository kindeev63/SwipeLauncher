package com.kindeev.swipelauncher.presentation.uiElements.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.DefaultImagesValues
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage


@Composable
fun PickDefaultImageDialog(
    picked: DefaultImage?,
    onPick: (DefaultImage) -> Unit,
    onDismissRequest: () -> Unit
) {
    var picked by remember {
        mutableStateOf(picked)
    }
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .heightIn(max = (screenConfiguration.screenHeightDp / 3 * 2).dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(((screenConfiguration.screenWidthDp - 20) / 50).toInt())
            ) {
                items(
                    items = DefaultImagesValues.defaultImages.keys.toList()
                ) { defaultImage ->
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .background(if (defaultImage == picked) Color.Gray.copy(alpha = 0.5f) else Color.Transparent)
                            .clickable { picked = defaultImage },
                        painter = painterResource(
                            id = DefaultImagesValues.defaultImages[defaultImage] ?: R.drawable.ic_error
                        ),
                        contentDescription = null
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(text = "Cancel")
                }
                TextButton(
                    onClick = {
                        picked?.let{
                            onPick(it)
                        }
                    }
                ) {
                    Text(text = "Save")
                }
            }
        }

    }
}