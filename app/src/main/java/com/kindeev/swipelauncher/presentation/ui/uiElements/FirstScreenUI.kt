package com.kindeev.swipelauncher.presentation.ui.uiElements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.getRootCircleMenu
import com.kindeev.swipelauncher.domain.isMyLauncherDefault
import com.kindeev.swipelauncher.domain.showLauncherSelection
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen

@Composable
fun FirstScreenUI() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        LauncherData.insertCircleMenu(getRootCircleMenu(context.resources.getString(R.string.root)))
        LauncherData.insertSettings(Constants.defaultSettings)
    }
    if (!context.isMyLauncherDefault()) {
        context.showLauncherSelection()
    }
    LauncherScreen()
}