package com.kindeev.swipelauncher.presentation.activities

import android.R.id.content
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.getAllApplicationInfo
import com.kindeev.swipelauncher.domain.getLauncherStatusBarStyle
import com.kindeev.swipelauncher.domain.getOnlyChanged
import com.kindeev.swipelauncher.domain.getRootCircleMenu
import com.kindeev.swipelauncher.domain.getUserImages
import com.kindeev.swipelauncher.domain.getValueOf
import com.kindeev.swipelauncher.domain.isMyLauncherDefault
import com.kindeev.swipelauncher.domain.registerAppsReceiver
import com.kindeev.swipelauncher.domain.removeUnusedUserImages
import com.kindeev.swipelauncher.domain.setActionAndImageTypes
import com.kindeev.swipelauncher.domain.unregisterAppsReceiver
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

class MainActivity : ComponentActivity() {
    private val appsReceiver = AppsReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        registerAppsReceiver(appsReceiver)
        setActionAndImageTypes()
        setContent {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            LauncherScreenTheme {
                var startDestination by rememberSaveable {
                    mutableStateOf(ScreensOnBoarding.MainScreenObject.route)
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
                                        getRootCircleMenu(
                                            context.resources.getString(
                                                R.string.root
                                            )
                                        )
                                    )
                                    LauncherData.insertSettings(Constants.defaultSettings)
                                }
                                onBoardingComplete()
                                navigationState.navHostController.popBackStack()
                                navigationState.navigateTo(ScreensOnBoarding.MainScreenObject.route)
                            }
                        )
                    },
                    startDestination = startDestination
                )
                LaunchedEffect(Unit) {
                    startDestination = if (isFirstRun()) {
                        if (context.isMyLauncherDefault()) {
                            LauncherData.insertCircleMenu(
                                getRootCircleMenu(
                                    context.resources.getString(
                                        R.string.root
                                    )
                                )
                            )
                            LauncherData.insertSettings(Constants.defaultSettings)
                            onBoardingComplete()
                            ScreensOnBoarding.MainScreenObject.route
                        } else {
                            ScreensOnBoarding.OnBoardingScreenObject.route
                        }
                    } else ScreensOnBoarding.MainScreenObject.route
                }
            }
        }
        LauncherData.allApplicationData.observe(this, object : Observer<List<ApplicationData>> {
            override fun onChanged(value: List<ApplicationData>) {
                CoroutineScope(Dispatchers.IO).launch {
                    LauncherData.setAllApplications(getAllApplicationInfo())
                    LauncherData.allCircleMenus.value?.let { allCircleMenus ->
                        removeUnusedUserImages(
                            allCircleMenus,
                            LauncherData.allApplicationData.value ?: emptyList()
                        )
                        LauncherData.userImages = getUserImages()
                        val changedCircleMenus =
                            allCircleMenus.getOnlyChanged(this@MainActivity)
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
                            allCircleMenus.getOnlyChanged(this@MainActivity)
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
        LauncherData.settings.observe(this, object : Observer<List<SettingData>> {
            override fun onChanged(value: List<SettingData>) {
                LauncherData.setTextColorOnWallpaper(
                    if (value.getValueOf(
                            SettingNames.BlackTextColorOnWallpaper,
                            BlackTextColorOnWallpaper::class.java
                        )?.enabled == true
                    ) Color.Black else Color.White
                )
            }
        })
    }

    private fun isFirstRun(): Boolean {
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        return !prefs.contains("first_run")
    }

    private fun onBoardingComplete() {
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("first_run", "false")
        editor.apply()
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