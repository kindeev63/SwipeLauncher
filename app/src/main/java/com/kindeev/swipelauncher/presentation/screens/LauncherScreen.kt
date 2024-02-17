package com.kindeev.swipelauncher.presentation.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.presentation.uiElements.ClockWidget
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.SwipeBoxUI
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.AllAppsBottomSheet
import kotlinx.coroutines.launch


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