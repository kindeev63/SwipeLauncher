package com.kindeev.swipelauncher.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.clickableClockSettingValue
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.domain.viewModels.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.domain.viewModels.launcherScreen.LauncherScreenVMFactory
import com.kindeev.swipelauncher.presentation.uiElements.ClickableClockWidget
import com.kindeev.swipelauncher.presentation.uiElements.ClockWidget
import com.kindeev.swipelauncher.presentation.uiElements.SearchBox
import com.kindeev.swipelauncher.presentation.uiElements.SwipeBox


@Composable
fun LauncherScreen() {
    val context = LocalContext.current
    val viewModel: LauncherScreenVM = viewModel(
        factory = LauncherScreenVMFactory(context = context)
    )
    val screenState by viewModel.screenState.observeAsState(LauncherScreenState.SwipeBox)
    BackHandler {}
    LauncherData.allCircleMenus.observe(LocalLifecycleOwner.current) { allMenus ->
        viewModel.setCircleMenu(
            allMenus.find { it.id == viewModel.circleMenu.value?.id }
                ?: allMenus.find { it.id == 0 }
        )
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (screenState) {
            LauncherScreenState.SwipeBox -> {
                ScreenContent(viewModel)
            }

            LauncherScreenState.SearchBox -> {
                SearchBox(viewModel = viewModel)
            }
        }
    }
}



@Composable
private fun ScreenContent(viewModel: LauncherScreenVM) {
    val clickableClockSetting = clickableClockSettingValue()
    SwipeBox(viewModel = viewModel)
    Column {
        Spacer(modifier = Modifier.fillMaxHeight(0.15f))
        if (clickableClockSetting.enabled) {
            ClickableClockWidget {
                clickableClockSetting.circleMenuAction?.let { viewModel.executeAction(it) }
            }
        } else {
            ClockWidget()
        }
    }
}