package com.kindeev.swipelauncher.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kindeev.swipelauncher.presentation.ui.theme.SettingsScreenTheme
import com.kindeev.swipelauncher.presentation.screens.SettingsScreen

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreenTheme {
                SettingsScreen()
            }
        }
    }
}