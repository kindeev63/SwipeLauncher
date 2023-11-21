package com.kindeev.swipelauncher.presentation.uiElements

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
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
    mainAppViewModel.allCircleMenu.observe(LocalLifecycleOwner.current) { circleMenus ->
        circleMenus.find { it.id == 0 }?.let { rootCircleMenu ->
            viewModel.setCircleMenu(circleMenu = rootCircleMenu)
        }

    }
    viewModel.circleMenu.observe(LocalLifecycleOwner.current) { circleMenu ->
        viewModel.setMenuIcons(
            menuImages = MenuImages(
                upImage = circleMenu.directionUp.image,
                downImage = circleMenu.directionDown.image,
                rightImage = circleMenu.directionRight.image,
                leftImage = circleMenu.directionLeft.image
            )
        )
    }
    val wallpaperManager = WallpaperManager.getInstance(context)
    var wallpaperDrawable: Drawable? = null
    val permission =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_EXTERNAL_STORAGE else Manifest.permission.READ_MEDIA_IMAGES
    if (ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED) {
        wallpaperDrawable = wallpaperManager.drawable
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(permission),
            0
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
                        val rootCircleMenu =
                            mainAppViewModel.allCircleMenu.value?.find { it.id == 0 }
                                ?: RootCircleMenu.rootCircleMenu
                        viewModel.stopDrag(rootCircleMenu)
                    }
                }
                return@pointerInteropFilter true
            }
    ) {
        wallpaperDrawable?.let {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = it.toBitmap().asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        CircleMenuUI(
            viewModel = viewModel,
            menuSize = viewModel.menuSize,
        )
    }
}