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
import com.kindeev.swipelauncher.domain.DataObject
import com.kindeev.swipelauncher.domain.DataObject.AppDataObject.getNotEmptyAllApplicationData
import com.kindeev.swipelauncher.domain.DataObject.CircleMenuDataObject.checkCircleMenuReturn
import com.kindeev.swipelauncher.domain.DataObject.CircleMenuDataObject.setUserImages
import com.kindeev.swipelauncher.domain.DataObject.ReceiverDataObject.registerAppsReceiver
import com.kindeev.swipelauncher.domain.DataObject.ReceiverDataObject.unregisterAppsReceiver
import com.kindeev.swipelauncher.data.ui.theme.LauncherScreenTheme
import com.kindeev.swipelauncher.presentation.MainApp
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.uiElements.FirstScreenUI

class MainActivity : ComponentActivity() {
    private val appsReceiver = AppsReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        val mainAppViewModel = (application as MainApp).mainAppVM
        registerAppsReceiver(appsReceiver)
        setContent {
            LauncherScreenTheme {
                if (isFirstRun()) {
                    FirstScreenUI(
                        mainAppVM = mainAppViewModel
                    )
                } else {
                    LauncherScreen(mainAppVM = mainAppViewModel)
                }
                mainAppViewModel.allCircleMenu.observe(this) { allCircleMenus ->
                    Thread {
                        val allApplicationData = getNotEmptyAllApplicationData(this)
                        if (DataObject.CircleMenuDataObject.userImages.isEmpty()) {
                            setUserImages(mainAppVM = mainAppViewModel, context = this)
                        }
                        val changedCircleMenus = checkCircleMenuReturn(
                            allCircleMenus = allCircleMenus,
                            allAppData = allApplicationData,
                            context = this
                        )
                        Handler(Looper.getMainLooper()).post {
                            if (changedCircleMenus.isNotEmpty()) mainAppViewModel.insertCircleMenus(
                                changedCircleMenus
                            )
                        }
                    }.start()
                }
            }
        }
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