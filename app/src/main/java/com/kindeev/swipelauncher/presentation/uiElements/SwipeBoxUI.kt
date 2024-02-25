package com.kindeev.swipelauncher.presentation.uiElements

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.viewModels.SwipeScreenViewModel
import com.kindeev.swipelauncher.presentation.viewModels.factories.SwipeScreenViewModelFactory

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBoxUI(
    mainAppViewModel: MainAppViewModel,
    onDoubleClick: () -> Unit
) {
    val viewModel: SwipeScreenViewModel = viewModel(
        factory = SwipeScreenViewModelFactory(
            context = LocalContext.current,
            mainAppViewModel = mainAppViewModel
        )
    )
    val circleMenu by viewModel.circleMenu.observeAsState()
    mainAppViewModel.allCircleMenu.observe(LocalLifecycleOwner.current) { allMenus ->
        viewModel.setCircleMenu(allMenus.find { it.id == viewModel.circleMenu.value?.id }
            ?: allMenus.find { it.id == 0 })
    }
    circleMenu?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter(
                    onTouchEvent = viewModel.onSwipe(
                        onDoubleClick = onDoubleClick
                    )
                )
        ) {
            CircleMenuUI(
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun CircleMenuUI(
    viewModel: SwipeScreenViewModel,
    centerCircleColor: Color = Color.Blue,
    centerCircleStroke: Stroke = Stroke(
        width = 5f
    ),
    itemCircleColor: Color = Color.Red,
    itemCircleStroke: Stroke = Stroke(
        width = 5f
    )
) {
    val menuOffsetState = viewModel.menuOffset.observeAsState()
    val menuOffset = menuOffsetState.value ?: return
    val circleMenu = viewModel.circleMenu.observeAsState()
    val menuSize = viewModel.menuSize

    val density = LocalDensity.current.density
    Box(
        modifier = Modifier
            .offset(
                x = (menuOffset.start.x).dp - (menuSize / 2).dp,
                y = menuOffset.start.y.dp - (menuSize / 2).dp
            )
            .size(menuSize.dp)
    ) {
        // Drawing items
        circleMenu.value?.menuImages?.let { menuImages ->
            CircleMenuImagesUI(
                menuSize = menuSize,
                menuImages = menuImages,
                itemCircleColor = itemCircleColor,
                itemCircleStroke = itemCircleStroke
            )
        }
        // Drawing a center circle
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                center = viewModel.getCenterCircleCords(
                    boarderOffset = (menuSize / 2) * density,
                    x = (menuOffset.swipe.x - menuOffset.start.x) * density,
                    y = (menuOffset.swipe.y - menuOffset.start.y) * density
                ),
                color = centerCircleColor,
                style = centerCircleStroke,
                radius = menuSize * density / 5.8f
            )
        }
    }
}