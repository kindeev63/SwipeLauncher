package com.kindeev.swipelauncher.presentation.activities

import android.R.id.content
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kindeev.swipelauncher.domain.utils.checkDirs
import com.kindeev.swipelauncher.domain.utils.getLauncherStatusBarStyle
import com.kindeev.swipelauncher.presentation.ui.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.ui.screens.OnboardingScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.DI
import com.kindeev.swipelauncher.presentation.navigation.MainActivityNav
import com.kindeev.swipelauncher.presentation.ui.theme.LauncherTheme
import com.kindeev.swipelauncher.presentation.viewModels.MainActivityVM
import com.kindeev.swipelauncher.presentation.viewModels.diViewModel
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        checkDirs()
        setContent {
            LauncherTheme {
                val viewModel: MainActivityVM = diViewModel()
                val navigationBackStack by viewModel.navigationBackStack.collectAsStateWithLifecycle()
                NavDisplay(
                    backStack = navigationBackStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    onBack = viewModel::navigationOnBack,
                    entryProvider = entryProvider {
                        entry<MainActivityNav.Launcher> {
                            val viewModel: LauncherScreenVM = diViewModel()
                            LauncherScreen(viewModel)
                        }
                        entry<MainActivityNav.OnBoarding> {
                            OnboardingScreen(
                                onFinish = viewModel::onCompleteOnBoarding
                            )
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableEdgeToEdge(
            statusBarStyle = getLauncherStatusBarStyle(
                DI.container.getSingle<SettingsStateFlowUseCase>().settings.value.blackTextColorOnWallpaper
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