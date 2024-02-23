package com.kindeev.swipelauncher.presentation.screens

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.data.DataObject.getAs
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.presentation.activities.SettingsActivity
import com.kindeev.swipelauncher.presentation.uiElements.ClickableClockWidget
import com.kindeev.swipelauncher.presentation.uiElements.ClockWidget
import com.kindeev.swipelauncher.presentation.uiElements.SearchBox
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.SwipeBoxUI
import kotlinx.coroutines.launch

@Composable
fun LauncherScreen(
    mainAppViewModel: MainAppViewModel
) {
    val context = LocalContext.current
    var showSearchBox by remember {
        mutableStateOf(false)
    }
    BackHandler {}

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (showSearchBox) {
            SearchBox(
                mainAppViewModel = mainAppViewModel,
                onDismissRequest = { showSearchBox = false },
                onLongClick = {
                    val intent = Intent(context, SettingsActivity::class.java)
                    context.startActivity(intent)
                    showSearchBox = false
                }
            )
        } else {
            SwipeBoxUI(
                mainAppViewModel = mainAppViewModel,
                onDoubleClick = { showSearchBox = true }
            )
            ScreenContent(
                mainAppViewModel = mainAppViewModel
            )
        }
    }

}


@Composable
private fun ScreenContent(
    mainAppViewModel: MainAppViewModel
) {
    val allSettings by mainAppViewModel.allSettings.observeAsState(emptyList())
    val clickableClockSetting = DataObject.SettingDataObject.clickableClockSettingValue(allSettings)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Column {
        Spacer(modifier = Modifier.fillMaxHeight(0.15f))
        if (clickableClockSetting.enabled) {
            ClickableClockWidget {
                when (clickableClockSetting.circleMenuAction?.type) {

                    CircleMenuActionTypes.OpenSettings -> {
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                    }

                    CircleMenuActionTypes.OpenApp -> {
                        val currentApp =
                            clickableClockSetting.circleMenuAction.data.getAs(OpenApp::class.java)
                        DataObject.openApp(currentApp.packageName, context)
                    }

                    CircleMenuActionTypes.FlashLightOn -> {
                        scope.launch {
                            val cameraManager =
                                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                            cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
                            mainAppViewModel.flashLightCondition = true
                        }
                    }

                    CircleMenuActionTypes.FlashLightOff -> {
                        scope.launch {
                            val cameraManager =
                                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                            cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
                            mainAppViewModel.flashLightCondition = false
                        }
                    }

                    CircleMenuActionTypes.ChangeFlashLightCondition -> {
                        scope.launch {
                            val cameraManager =
                                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                            cameraManager.setTorchMode(
                                cameraManager.cameraIdList[0],
                                !mainAppViewModel.flashLightCondition
                            )
                            mainAppViewModel.flashLightCondition =
                                !mainAppViewModel.flashLightCondition
                        }
                    }

                    else -> {}
                }
            }
        } else {
            ClockWidget()
        }
    }
}