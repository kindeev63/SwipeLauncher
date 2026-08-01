package com.kindeev.swipelauncher.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.presentation.ui.elements.ClickableClockWidget
import com.kindeev.swipelauncher.presentation.ui.elements.ClockWidget
import com.kindeev.swipelauncher.presentation.ui.elements.SwipeBoxUI
import com.kindeev.swipelauncher.presentation.ui.elements.searchBox.SearchBoxUI
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LauncherScreen(
    viewModel: LauncherScreenVM
) {
    val screenState by viewModel.screenState.collectAsState()
    BackHandler {}

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
                SearchBoxUI(
                    viewModel = viewModel,
                    onClose = { viewModel.closeSearchBox() }
                )
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.closeSearchBox()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


@Composable
private fun ScreenContent(viewModel: LauncherScreenVM) {
    val settings by viewModel.settingsStateFlowUseCase.settings.collectAsState()
    val scope = rememberCoroutineScope()
    SwipeBoxUI(viewModel = viewModel)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.15f))
        if (settings.clickOnClock.enable) {
            ClickableClockWidget(
                textColor =
                    if (settings.blackTextColorOnWallpaper) Color.Black else Color.White
            ) {
                scope.launch {
                    viewModel.executeAction(settings.clickOnClock.action)
                }

            }
        } else {
            ClockWidget(
                textColor =
                    if (settings.blackTextColorOnWallpaper) Color.Black else Color.White
            )
        }
    }
}