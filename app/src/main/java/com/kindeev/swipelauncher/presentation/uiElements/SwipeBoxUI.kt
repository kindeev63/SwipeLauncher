package com.kindeev.swipelauncher.presentation.uiElements

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
    mainAppViewModel: MainAppViewModel
) {
    val viewModel: SwipeScreenViewModel = viewModel(
        factory = SwipeScreenViewModelFactory(
            context = LocalContext.current,
            mainAppViewModel = mainAppViewModel
        )
    )
    mainAppViewModel.allCircleMenu.observe(LocalLifecycleOwner.current) { allMenus ->
        viewModel.circleMenu.value?.let { current -> allMenus.find { it.id == current.id }?.let { viewModel.setCircleMenu(it) } }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter(
                onTouchEvent = viewModel.onSwipe()
            )
    ) {
        CircleMenuUI(
            viewModel = viewModel,
            menuSize = viewModel.menuSize,
        )
    }
}

@Composable
private fun CircleMenuUI(
    viewModel: SwipeScreenViewModel,
    menuSize: Float,
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

    val density = LocalDensity.current.density
    Box(
        modifier = Modifier
            .offset(
                x = (menuOffset.start.x).dp - (menuSize / 2).dp,
                y = menuOffset.start.y.dp - (menuSize / 2).dp
            )
            .size(menuSize.dp)
    ) {
        Log.e("test", circleMenu.value?.menuImages.toString())
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
                radius = menuSize / 2
            )
        }
    }
}