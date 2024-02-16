package com.kindeev.swipelauncher.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kindeev.swipelauncher.data.ui.theme.SwipeLauncherTheme
import com.kindeev.swipelauncher.presentation.MainApp
import com.kindeev.swipelauncher.presentation.screens.SettingsScreen

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainAppViewModel = (application as MainApp).mainAppViewModel
            SwipeLauncherTheme() {
                SettingsScreen(mainAppViewModel = mainAppViewModel)
            }
        }
    }
}