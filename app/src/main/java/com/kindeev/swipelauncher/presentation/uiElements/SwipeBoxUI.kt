package com.kindeev.swipelauncher.presentation.uiElements

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import android.os.Vibrator
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.data.MenuImages
import com.kindeev.swipelauncher.data.RootCircleMenu
import com.kindeev.swipelauncher.presentation.MainAppViewModel
import com.kindeev.swipelauncher.presentation.SwipeScreenViewModel
import com.kindeev.swipelauncher.presentation.SwipeScreenViewModelFactory

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBoxUI(
    mainAppViewModel: MainAppViewModel
) {
    val viewModel: SwipeScreenViewModel = viewModel(
        factory = SwipeScreenViewModelFactory(context = LocalContext.current)
    )
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    mainAppViewModel.allCircleMenu.observe(LocalLifecycleOwner.current) { circleMenus ->
        circleMenus.find { it.id == 0 }?.let { rootCircleMenu ->
            viewModel.setCircleMenu(circleMenu = rootCircleMenu)
        }

    }
    viewModel.circleMenu.observe(LocalLifecycleOwner.current) { circleMenu ->
        viewModel.setMenuIcons(
            menuImages = MenuImages(
                upImage = circleMenu.upImage,
                downImage = circleMenu.downImage,
                rightImage = circleMenu.rightImage,
                leftImage = circleMenu.leftImage
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN ->
                        viewModel.startDrag(
                            x = event.x / density,
                            y = event.y / density
                        )

                    MotionEvent.ACTION_MOVE ->
                        viewModel.drag(
                            x = event.x / density,
                            y = event.y / density,
                            context = context
                        )

                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        val rootCircleMenu = mainAppViewModel.allCircleMenu.value?.find { it.id == 0 }
                            ?: RootCircleMenu.rootCircleMenu
                        viewModel.stopDrag(rootCircleMenu)
                    }
                }
                return@pointerInteropFilter true
            }
    ) {
        CircleMenuUI(
            viewModel = viewModel,
            menuSize = viewModel.menuSize,
        )
    }
}