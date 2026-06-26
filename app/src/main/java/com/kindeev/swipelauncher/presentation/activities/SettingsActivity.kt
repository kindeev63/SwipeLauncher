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
import com.kindeev.swipelauncher.data.userImages.getUsedImageIds
import com.kindeev.swipelauncher.di.container
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.presentation.ui.theme.SettingsScreenTheme
import com.kindeev.swipelauncher.presentation.screens.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {

    private val applicationsUseCase = ApplicationsUseCase(this)
    private val checkCircleMenuUseCase by lazy { CheckCircleMenuUseCase(container.userImagesRepository, applicationsUseCase) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContent {
            SettingsScreenTheme {
                SettingsScreen()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            container.circleMenus.collect { allCircleMenus ->
                container.setApplications(applicationsUseCase.getAllApplicationInfo())
                container.userImagesRepository.removeUnused(
                    getUsedImageIds(
                        allCircleMenus,
                        container.applicationsData.value
                    )
                )
                val changedCircleMenus =
                    checkCircleMenuUseCase.getOnlyChanged(allCircleMenus)
                Handler(Looper.getMainLooper()).post {
                    if (changedCircleMenus.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            container.dataRepository.insertCircleMenus(
                                changedCircleMenus
                            )
                        }
                    }
                }
            }
        }
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