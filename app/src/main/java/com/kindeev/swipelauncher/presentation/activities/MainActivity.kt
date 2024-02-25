package com.kindeev.swipelauncher.presentation.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import com.kindeev.swipelauncher.data.DataObject.AppDataObject.getAllApplicationData
import com.kindeev.swipelauncher.data.DataObject.AppDataObject.setAllApplicationData
import com.kindeev.swipelauncher.data.DataObject.CircleMenuDataObject.checkCircleMenuReturn
import com.kindeev.swipelauncher.data.DataObject.CircleMenuDataObject.setUserImages
import com.kindeev.swipelauncher.data.DataObject.ReceiverDataObject.registerAppsReceiver
import com.kindeev.swipelauncher.data.DataObject.ReceiverDataObject.unregisterAppsReceiver
import com.kindeev.swipelauncher.data.ui.theme.LauncherScreenTheme
import com.kindeev.swipelauncher.presentation.MainApp
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.uiElements.FirstScreenUI
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel

class MainActivity : ComponentActivity() {
    private val appsReceiver = AppsReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        setDataInNewThread(mainAppViewModel)
        registerAppsReceiver(appsReceiver)
        setContent {
            LauncherScreenTheme {
                if (isFirstRun()) {
                    FirstScreenUI(
                        mainAppViewModel = mainAppViewModel
                    )
                } else {
                    LauncherScreen(mainAppViewModel = mainAppViewModel)
                }
                mainAppViewModel.allCircleMenu.observe(this) { allCircleMenus ->
                    val handler = Handler(Looper.getMainLooper())
                    Thread {
                        val changedCircleMenus = checkCircleMenuReturn(
                            allCircleMenus = allCircleMenus,
                            context = this
                        )
                        handler.post {
                            if (changedCircleMenus.isNotEmpty()) mainAppViewModel.insertCircleMenus(
                                changedCircleMenus
                            )
                        }
                    }.start()
                }
            }
        }
    }

    private fun setDataInNewThread(
        mainAppViewModel: MainAppViewModel
    ) {
        val handler = Handler(Looper.getMainLooper())
        Thread {
            setUserImages(mainAppViewModel = mainAppViewModel, context = this)
            val allApplicationData = getAllApplicationData()
            handler.post {
                setAllApplicationData(allApplicationData)
            }
        }.start()
    }

    private fun isFirstRun(): Boolean {
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        return if (prefs.contains("first_run")) {
            false
        } else {
            val editor = prefs.edit()
            editor.putString("first_run", "false")
            editor.apply()
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterAppsReceiver(appsReceiver)
    }
}