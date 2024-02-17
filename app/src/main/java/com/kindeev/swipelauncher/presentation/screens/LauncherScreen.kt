package com.kindeev.swipelauncher.presentation.screens

import android.Manifest
import android.app.Instrumentation
import android.app.WallpaperManager
import android.os.Build
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.kindeev.swipelauncher.presentation.uiElements.ClockWidget
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.SwipeBoxUI
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.AllAppsBottomSheet
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.rememberPermissionState
import com.kindeev.swipelauncher.R


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LauncherScreen(
    mainAppViewModel: MainAppViewModel
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    BackHandler {
        if (sheetState.isVisible) {
            scope.launch {
                sheetState.hide()
            }
        } else {
            Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        }
    }
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = Color.Transparent,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = {
            AllAppsBottomSheet(sheetState = sheetState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            PhoneWallpaper()
            ScreenContent()
            SwipeBoxUI(
                mainAppViewModel = mainAppViewModel,
                openSheet = {
                    scope.launch {
                        sheetState.show()
                    }
                }
            )
        }
    }

}


@Composable
private fun ScreenContent() {
    Column {
        Spacer(modifier = Modifier.fillMaxHeight(0.15f))
        ClockWidget()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PhoneWallpaper() {
    val wallpaperManager = WallpaperManager.getInstance(LocalContext.current)
    val permissionState = rememberPermissionState(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    LaunchedEffect(permissionState) {
        if (permissionState.status != PermissionStatus.Granted) {
            permissionState.launchPermissionRequest()
        }
    }
    if (permissionState.status == PermissionStatus.Granted) {
        val imageBitmap = wallpaperManager.drawable?.toBitmap()?.asImageBitmap()
        if (imageBitmap == null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.wallapaper),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = imageBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.wallapaper),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}