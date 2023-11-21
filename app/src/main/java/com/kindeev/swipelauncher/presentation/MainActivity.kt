package com.kindeev.swipelauncher.presentation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.presentation.uiElements.FirstScreenUI
import com.kindeev.swipelauncher.presentation.uiElements.SwipeBoxUI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        makeStatusBarTransparent(this)
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

    private fun makeStatusBarTransparent(activity: Activity) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.decorView.systemUiVisibility = option
            window.statusBarColor = Color.Transparent.value.toInt()
    }
}

