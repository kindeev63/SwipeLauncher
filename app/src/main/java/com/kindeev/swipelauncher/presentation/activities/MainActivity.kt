package com.kindeev.swipelauncher.presentation.activities

import android.R.id.content
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.utils.checkDirs
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.GetRootCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.UserImagesUseCase
import com.kindeev.swipelauncher.domain.utils.getLauncherStatusBarStyle
import com.kindeev.swipelauncher.domain.utils.getValueOf
import com.kindeev.swipelauncher.domain.utils.isMyLauncherDefault
import com.kindeev.swipelauncher.domain.utils.registerAppsReceiver
import com.kindeev.swipelauncher.domain.utils.setActionAndImageTypes
import com.kindeev.swipelauncher.domain.utils.unregisterAppsReceiver
import com.kindeev.swipelauncher.presentation.navigation.OnBoardingNavGraph
import com.kindeev.swipelauncher.presentation.navigation.ScreensOnBoarding
import com.kindeev.swipelauncher.presentation.navigation.rememberNavigationState
import com.kindeev.swipelauncher.presentation.ui.theme.LauncherScreenTheme
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.screens.OnboardingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.content.edit

class MainActivity : ComponentActivity() {

    private val appsReceiver = AppsReceiver()

    private val getRootCircleMenuUseCase = GetRootCircleMenuUseCase(this)
    private val userImagesUseCase = UserImagesUseCase(this)
    private val applicationsUseCase = ApplicationsUseCase(this)
    private val checkCircleMenuUseCase =
        CheckCircleMenuUseCase(userImagesUseCase, applicationsUseCase)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        checkDirs()
        registerAppsReceiver(appsReceiver)
        setActionAndImageTypes()
        setContent {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            LauncherScreenTheme {
                var startDestination by remember {
                    mutableStateOf<ScreensOnBoarding>(ScreensOnBoarding.MainScreenObject)
                }
                val navigationState = rememberNavigationState()
                OnBoardingNavGraph(
                    navHostController = navigationState.navHostController,
                    mainScreen = {
                        LauncherScreen()
                    },
                    onboardingScreen = {
                        OnboardingScreen(
                            onFinish = {
                                scope.launch {
                                    LauncherData.insertCircleMenu(
                                        getRootCircleMenuUseCase.get(
                                            context.resources.getString(
                                                R.string.root
                                            )
                                        )
                                    )
                                }
                                onBoardingComplete()
                                navigationState.navHostController.popBackStack()
                                navigationState.navigateTo(ScreensOnBoarding.MainScreenObject)
                            }
                        )
                    },
                    startDestination = startDestination
                )
                LaunchedEffect(Unit) {
                    startDestination = if (isFirstRun()) {
                        if (context.isMyLauncherDefault()) {
                            LauncherData.insertCircleMenu(
                                getRootCircleMenuUseCase.get(
                                    context.resources.getString(
                                        R.string.root
                                    )
                                )
                            )
                            onBoardingComplete()
                            ScreensOnBoarding.MainScreenObject
                        } else {
                            ScreensOnBoarding.OnBoardingScreenObject
                        }
                    } else ScreensOnBoarding.MainScreenObject
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                LauncherData.allApplicationData.collect { allApplicationData ->
                    LauncherData.setAllApplications(
                        applicationsUseCase.getAllApplicationInfo()
                    )
                    userImagesUseCase.removeUnusedUserImages(
                        LauncherData.allCircleMenus.value,
                        allApplicationData
                    )
                    LauncherData.userImages = userImagesUseCase.getUserImages()
                    val changedCircleMenus =
                        checkCircleMenuUseCase.getOnlyChanged(LauncherData.allCircleMenus.value)
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
            launch {
                LauncherData.allCircleMenus.collect { allCircleMenus ->
                    LauncherData.setAllApplications(applicationsUseCase.getAllApplicationInfo())
                    userImagesUseCase.removeUnusedUserImages(
                        allCircleMenus,
                        LauncherData.allApplicationData.value
                    )
                    LauncherData.userImages = userImagesUseCase.getUserImages()
                    val changedCircleMenus =
                        checkCircleMenuUseCase.getOnlyChanged(allCircleMenus)
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
            launch {
                LauncherData.settings.collect { settings ->
                    LauncherData.setTextColorOnWallpaper(
                        if (settings.getValueOf(
                                SettingNames.BlackTextColorOnWallpaper,
                                BlackTextColorOnWallpaper::class.java
                            )?.enabled == true
                        ) Color.Black else Color.White
                    )
                }
            }
        }
    }

    private fun isFirstRun(): Boolean {
        val prefs = getSharedPreferences("data", MODE_PRIVATE)
        return !prefs.contains("first_run")
    }

    private fun onBoardingComplete() {
        val prefs = getSharedPreferences("data", MODE_PRIVATE)
        prefs.edit {
            putString("first_run", "false")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterAppsReceiver(appsReceiver)
    }

    override fun onResume() {
        super.onResume()
        enableEdgeToEdge(statusBarStyle = getLauncherStatusBarStyle())
    }

    private fun hideNavigationBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window,
            window.decorView.findViewById(content)
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}