package com.kindeev.swipelauncher.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kindeev.swipelauncher.presentation.uiElements.ClockWidget
import com.kindeev.swipelauncher.presentation.uiElements.SearchBox
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.SwipeBoxUI

@Composable
fun LauncherScreen(
    mainAppViewModel: MainAppViewModel
) {
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
                onDismissRequest = { showSearchBox = false }
            )
        } else {
            SwipeBoxUI(
                mainAppViewModel = mainAppViewModel,
                onDoubleClick = { showSearchBox = true }
            )
            ScreenContent()
        }
    }

}


@Composable
private fun ScreenContent() {
    Column {
        Spacer(modifier = Modifier.fillMaxHeight(0.15f))
        ClockWidget(
            onClick = {
//                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
//                context.startActivity(intent)
            }
        )

    }
}