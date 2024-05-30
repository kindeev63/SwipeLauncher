package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.viewModels.LauncherScreenVM

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBox(viewModel: LauncherScreenVM) {
    val currentMenu by viewModel.currentMenu.observeAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter(
                onTouchEvent = viewModel.onSwipe()
            )
    ) {
        currentMenu?.let {
            DrawCircleMenu(viewModel = viewModel)
        }
    }
}

@Composable
private fun DrawCircleMenu(
    viewModel: LauncherScreenVM,
) {
    val currentMenu by viewModel.currentMenu.observeAsState()
    currentMenu?.offset?.let { offset ->
        Box(
            modifier = Modifier
                .offset(
                    x = offset.x.dp - (viewModel.menuSize / 2).dp,
                    y = offset.y.dp - (viewModel.menuSize / 2).dp
                )
                .size(viewModel.menuSize.dp)
        ) {
            CircleMenuItems(items = currentMenu?.circleMenu?.items ?: emptyList(), menuSize = viewModel.menuSize)
        }
    }
}