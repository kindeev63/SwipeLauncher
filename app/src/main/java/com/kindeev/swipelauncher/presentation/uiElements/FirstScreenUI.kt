package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen

@Composable
fun FirstScreenUI(mainAppViewModel: MainAppViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue.copy(alpha = 0.1f))
    ) {
        End(mainAppViewModel = mainAppViewModel)
    }
}

@Composable
private fun End(mainAppViewModel: MainAppViewModel) {
    val context = LocalContext.current
    mainAppViewModel.insertCircleMenu(DataObject.getRootCircleMenu())
    DataObject.SettingDataObject.setDefaultSettings(mainAppViewModel = mainAppViewModel)
    if (!DataObject.isMyLauncherDefault(context)) {
        DataObject.showLauncherSelection(context)
    }
    LauncherScreen(mainAppViewModel = mainAppViewModel)
}