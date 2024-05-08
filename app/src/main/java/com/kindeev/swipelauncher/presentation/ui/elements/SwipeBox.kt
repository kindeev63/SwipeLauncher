package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.dataBase.MenuImages
import com.kindeev.swipelauncher.domain.viewModels.LauncherScreenVM

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBox(viewModel: LauncherScreenVM) {
    val circleMenuOffset by viewModel.circleMenuOffset.observeAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter(
                onTouchEvent = viewModel.onSwipe()
            )
    ) {
        circleMenuOffset?.let {
            DrawCircleMenu(viewModel = viewModel)
        }
    }
}

@Composable
private fun DrawCircleMenu(
    viewModel: LauncherScreenVM,
) {
    val density = LocalDensity.current.density
    val circleMenuOffset by viewModel.circleMenuOffset.observeAsState()
    val menuImages by viewModel.menuImages.observeAsState(initial = MenuImages.initial())
    circleMenuOffset?.let { menuOffset ->
        Box(
            modifier = Modifier
                .offset(
                    x = menuOffset.start.x.dp - (viewModel.menuSize / 2).dp,
                    y = menuOffset.start.y.dp - (viewModel.menuSize / 2).dp
                )
                .size(viewModel.menuSize.dp)
        ) {
            // Drawing items
            CircleMenuImagesUI(
                menuSize = viewModel.menuSize,
                menuImages = menuImages
            )
            // Drawing a center circle
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawCircle(
                    center = viewModel.centerCircleCenterOffset(),
                    color = Color.Blue,
                    style = Stroke(
                        width = 5f
                    ),
                    radius = viewModel.menuSize * density / 5.8f
                )
            }
        }
    }
}