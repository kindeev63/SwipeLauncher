package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.kindeev.swipelauncher.domain.DataObject.CircleMenuDataObject.getRootCircleMenu
import com.kindeev.swipelauncher.domain.DataObject.SettingDataObject.setDefaultSettings
import com.kindeev.swipelauncher.domain.DataObject.isMyLauncherDefault
import com.kindeev.swipelauncher.domain.DataObject.showLauncherSelection
import com.kindeev.swipelauncher.domain.viewModels.MainAppVM
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen

@Composable
fun FirstScreenUI(mainAppVM: MainAppVM) {
    val context = LocalContext.current
    mainAppVM.insertCircleMenu(getRootCircleMenu())
    setDefaultSettings(mainAppVM = mainAppVM)
    if (!isMyLauncherDefault(context)) {
        showLauncherSelection(context)
    }
    LauncherScreen(mainAppVM = mainAppVM)
}