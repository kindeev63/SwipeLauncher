package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen.LauncherScreenVM

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBoxUI(
    viewModel: LauncherScreenVM
) {
    val size = Constants.minScreenLength / 3f * 2
    val currentMenu by viewModel.currentMenu.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter(
                onTouchEvent = viewModel.onSwipe()
            )
    ) {
        currentMenu?.offset?.let { offset ->
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .offset(
                        x = offset.x.dp - size.dp / 2,
                        y = offset.y.dp - size.dp / 2,
                    )
            ) {
                currentMenu?.circleMenu?.items?.let { items ->
                    val itemsOffset = viewModel.getOffset()
                    items.forEachIndexed { index, item ->
                        viewModel.getItemImage(item.image)?.let { imageBitmap ->
                            Image(
                                modifier = Modifier
                                    .offset(
                                        x = itemsOffset[index].x,
                                        y = itemsOffset[index].y
                                    )
                                    .size(viewModel.itemSize),
                                bitmap = imageBitmap,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}