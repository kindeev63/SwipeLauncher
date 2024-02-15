package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    mainAppViewModel.insertCircleMenu(DataObject.getRootCircleMenu())
    LauncherScreen(mainAppViewModel = mainAppViewModel)
}