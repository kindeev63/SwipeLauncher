package com.kindeev.swipelauncher.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.ClickOnClock
import com.kindeev.swipelauncher.domain.utils.getValueOf
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen.LauncherScreenVMFactory
import com.kindeev.swipelauncher.presentation.ui.elements.ClickableClockWidget
import com.kindeev.swipelauncher.presentation.ui.elements.ClockWidget
import com.kindeev.swipelauncher.presentation.ui.elements.SwipeBoxUI
import com.kindeev.swipelauncher.presentation.ui.elements.searchBox.SearchBoxUI


@Composable
fun LauncherScreen() {
    val context = LocalContext.current
    val viewModel: LauncherScreenVM = viewModel(
        factory = LauncherScreenVMFactory(context = context)
    )
    val screenState by viewModel.screenState.observeAsState(LauncherScreenState.SwipeBox)
    BackHandler {}
    LauncherData.allCircleMenus.observe(LocalLifecycleOwner.current) { allMenus ->
        (allMenus.find { it.id == viewModel.currentMenu.value?.circleMenu?.id }
            ?: allMenus.find { it.id == 0 })?.let {
                viewModel.setCircleMenu(it)
            }
        viewModel.setOffsets(allMenus)
        viewModel.setSizes(allMenus)
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
            if (event == Lifecycle.Event.ON_PAUSE && !viewModel.userImageGetProcess) {
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
    val settings by LauncherData.settings.observeAsState(emptyList())
    val clickOnClock = settings.getValueOf(SettingNames.ClickOnClock, ClickOnClock::class.java)
    SwipeBoxUI(viewModel = viewModel)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.15f))
        if (clickOnClock?.enabled == true) {
            ClickableClockWidget {
                viewModel.executeAction(clickOnClock.action)
            }
        } else {
            ClockWidget()
        }
    }
}