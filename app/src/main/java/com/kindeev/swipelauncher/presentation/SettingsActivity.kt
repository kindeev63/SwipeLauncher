package com.kindeev.swipelauncher.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kindeev.swipelauncher.presentation.uiElements.EditCircleMenuScreen

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        setContent {
            EditCircleMenuScreen(
                mainAppViewModel = mainAppViewModel,
                circleMenuId = 0
            )
        }
    }
}