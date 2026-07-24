package com.kindeev.swipelauncher.presentation.activities

import android.R.id.content
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.utils.checkDirs
import com.kindeev.swipelauncher.domain.utils.getLauncherStatusBarStyle
import com.kindeev.swipelauncher.domain.utils.isMyLauncherDefault
import com.kindeev.swipelauncher.presentation.navigation.OnBoardingNavGraph
import com.kindeev.swipelauncher.presentation.navigation.ScreensOnBoarding
import com.kindeev.swipelauncher.presentation.navigation.rememberNavigationState
import com.kindeev.swipelauncher.presentation.ui.theme.LauncherScreenTheme
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.screens.OnboardingScreen
import androidx.core.content.edit
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.GetRootCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.DI
import com.kindeev.swipelauncher.presentation.viewModels.diViewModel
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        checkDirs()
        setContent {
            val context = LocalContext.current
            LauncherScreenTheme {
                var startDestination by remember {
                    mutableStateOf<ScreensOnBoarding>(ScreensOnBoarding.MainScreenObject)
                }
                val navigationState = rememberNavigationState()
                OnBoardingNavGraph(
                    navHostController = navigationState.navHostController,
                    mainScreen = {
                        val viewModel: LauncherScreenVM = diViewModel()
                        LauncherScreen(viewModel)
                    },
                    onboardingScreen = {
                        OnboardingScreen(
                            onFinish = {
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
                        insertRootCircleMenu()
                        if (context.isMyLauncherDefault()) {
                            onBoardingComplete()
                            ScreensOnBoarding.MainScreenObject
                        } else {
                            ScreensOnBoarding.OnBoardingScreenObject
                        }
                    } else ScreensOnBoarding.MainScreenObject
                }
            }
        }
    }

    private suspend fun insertRootCircleMenu() {
        DI.container.getDependency<DataRepository>()
            .insertCircleMenu(
                DI.container.getDependency<GetRootCircleMenuUseCase>()
                    .get(
                        resources.getString(
                            R.string.root
                        )
                    )
            )
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

    override fun onResume() {
        super.onResume()
        enableEdgeToEdge(
            statusBarStyle = getLauncherStatusBarStyle(
                DI.container.getDependency<SettingsStateFlowUseCase>().settings.value.blackTextColorOnWallpaper
            )
        )
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