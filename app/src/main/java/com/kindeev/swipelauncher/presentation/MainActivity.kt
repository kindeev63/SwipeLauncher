package com.kindeev.swipelauncher.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.presentation.uiElements.FirstScreenUI
import com.kindeev.swipelauncher.presentation.uiElements.SwipeBoxUI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        setContent {
            var allCircleMenu by remember {
                mutableStateOf<List<CircleMenu>?>(null)
            }
            mainAppViewModel.allCircleMenu.observe(this) {
                allCircleMenu = it
            }
            allCircleMenu?.let { circleMenus ->
                Log.e("test", circleMenus.isEmpty().toString())
                if (circleMenus.isEmpty()) {
                    FirstScreenUI(
                        mainAppViewModel = mainAppViewModel
                    )
                } else {
                    SwipeBoxUI(
                        mainAppViewModel = mainAppViewModel
                    )
                }
            }

        }
    }
}

