package com.kindeev.swipelauncher.presentation.activities

import android.R.id.content
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.getAllApplicationInfo
import com.kindeev.swipelauncher.domain.getOnlyChanged
import com.kindeev.swipelauncher.domain.getUserImages
import com.kindeev.swipelauncher.domain.removeUnusedUserImages
import com.kindeev.swipelauncher.presentation.ui.theme.SettingsScreenTheme
import com.kindeev.swipelauncher.presentation.screens.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContent {
            SettingsScreenTheme {
                SettingsScreen()
            }
        }
        LauncherData.allCircleMenus.observe(this, object : Observer<List<CircleMenu>> {
            override fun onChanged(value: List<CircleMenu>) {
                CoroutineScope(Dispatchers.IO).launch {
                    LauncherData.setAllApplications(getAllApplicationInfo())
                    LauncherData.allCircleMenus.value?.let { allCircleMenus ->
                        removeUnusedUserImages(
                            allCircleMenus,
                            LauncherData.allApplicationData.value ?: emptyList()
                        )
                        LauncherData.userImages = getUserImages()
                        val changedCircleMenus =
                            allCircleMenus.getOnlyChanged(this@SettingsActivity)
                        Handler(Looper.getMainLooper()).post {
                            if (changedCircleMenus.isNotEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    LauncherData.insertCircleMenus(
                                        changedCircleMenus
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun hideNavigationBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window,
            window.decorView.findViewById(content)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}