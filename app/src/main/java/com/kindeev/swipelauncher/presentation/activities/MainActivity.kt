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
import androidx.compose.runtime.rememberCoroutineScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.getAllApplicationData
import com.kindeev.swipelauncher.domain.getOnlyChanged
import com.kindeev.swipelauncher.domain.getUserImages
import com.kindeev.swipelauncher.domain.registerAppsReceiver
import com.kindeev.swipelauncher.domain.removeUnusedUserImages
import com.kindeev.swipelauncher.domain.setActionAndImageTypes
import com.kindeev.swipelauncher.domain.unregisterAppsReceiver
import com.kindeev.swipelauncher.presentation.ui.theme.LauncherScreenTheme
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.ui.elements.FirstScreenUI
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private val appsReceiver = AppsReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        registerAppsReceiver(appsReceiver)
        setActionAndImageTypes()
        setContent {
            val scope = rememberCoroutineScope()
            LauncherScreenTheme {
                if (isFirstRun()) {
                    FirstScreenUI()
                } else {
                    LauncherScreen()
                }
                LauncherData.allCircleMenus.observe(this) { allCircleMenus ->
                    thread {
                        LauncherData.setAllApplicationData(getAllApplicationData())
                        removeUnusedUserImages(allCircleMenus)
                        LauncherData.userImages = getUserImages()
                        val changedCircleMenus = allCircleMenus.getOnlyChanged(this)
                        Handler(Looper.getMainLooper()).post {
                            if (changedCircleMenus.isNotEmpty()) {
                                scope.launch {
                                    LauncherData.insertCircleMenus(
                                        changedCircleMenus
                                    )
                                }
                            }
                        }
                    }
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