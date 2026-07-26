package com.kindeev.swipelauncher.presentation.ui.elements

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
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBoxUI(
    viewModel: LauncherScreenVM
) {
    val currentMenuWithOffset by viewModel.currentMenuWithOffset.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter(
                onTouchEvent = viewModel.onSwipe()
            )
    ) {
        currentMenuWithOffset?.let { menu ->
            CircleMenuItems(
                modifier = Modifier
                    .size(menu.circleMenuToDraw.menuSize.dp)
                    .offset(
                        x = menu.offset.x.dp - menu.circleMenuToDraw.menuSize.dp / 2,
                        y = menu.offset.y.dp - menu.circleMenuToDraw.menuSize.dp / 2,
                    ),
                items = menu.circleMenuToDraw.items,
                itemSize = menu.circleMenuToDraw.itemSize
            )
        }
    }
}