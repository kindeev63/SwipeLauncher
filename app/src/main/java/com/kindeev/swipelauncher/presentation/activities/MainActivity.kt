package com.kindeev.swipelauncher.presentation.activities

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.data.DataObject.registerAppsReceiver
import com.kindeev.swipelauncher.data.DataObject.setAllApplicationData
import com.kindeev.swipelauncher.data.DataObject.unregisterAppsReceiver
import com.kindeev.swipelauncher.data.ui.theme.LauncherScreenTheme
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.presentation.MainApp
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.uiElements.FirstScreenUI

class MainActivity : ComponentActivity() {
    private val appsReceiver = AppsReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        setAllApplicationData()
        registerAppsReceiver(appsReceiver)
        setContent {
            LauncherScreenTheme {
                var allCircleMenu by remember {
                    mutableStateOf<List<CircleMenu>?>(null)
                }
                mainAppViewModel.allCircleMenu.observe(this) { allCircleMenus ->
                    allCircleMenu = allCircleMenus
                    val handler = Handler(Looper.getMainLooper())
                    val myThread = Thread {
                        DataObject.setUserImages(mainAppViewModel = mainAppViewModel, context = this)
                        val changedCircleMenus = DataObject.checkCircleMenuReturn(
                            allCircleMenus = allCircleMenus,
                            context = this
                        )
                        handler.post {
                            if (changedCircleMenus.isNotEmpty()) mainAppViewModel.insertCircleMenus(changedCircleMenus)
                        }
                    }
                    myThread.start()
                }

                allCircleMenu?.let { circleMenus ->
                    if (circleMenus.find { it.id == 0 } == null) {
                        FirstScreenUI(
                            mainAppViewModel = mainAppViewModel
                        )
                    } else {
                        LauncherScreen(mainAppViewModel = mainAppViewModel)
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterAppsReceiver(appsReceiver)
    }
}