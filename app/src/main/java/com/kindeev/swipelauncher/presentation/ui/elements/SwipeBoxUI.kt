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
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBoxUI(
    viewModel: LauncherScreenVM
) {
    val size = Constants.minScreenLength / 3f * 2
    val currentMenuWithOffset by viewModel.currentMenuWithOffset.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter(
                onTouchEvent = viewModel.onSwipe()
            )
    ) {
        currentMenuWithOffset?.let { menu ->
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .offset(
                        x = menu.offset.x.dp - size.dp / 2,
                        y = menu.offset.y.dp - size.dp / 2,
                    )
            ) {
                menu.circleMenuToDraw.items.forEach { item ->
                    Image(
                        bitmap = item.image,
                        modifier = Modifier
                            .offset(
                                x = item.offset.x.dp,
                                y = item.offset.y.dp
                            )
                            .size(menu.circleMenuToDraw.itemSize.dp),
                        contentDescription = null
                    )
                }
            }
        }
    }
}