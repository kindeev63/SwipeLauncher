package com.kindeev.swipelauncher.presentation.activities

import android.R.id.content
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.ui.dialogs.ActionDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.ImageDialog
import com.kindeev.swipelauncher.presentation.ui.screens.OnboardingScreen
import com.kindeev.swipelauncher.presentation.ui.screens.settings.additionalSettingsScreen.AdditionalSettingsScreen
import com.kindeev.swipelauncher.presentation.ui.screens.settings.allCircleMenusScreen.AllCircleMenusScreen
import com.kindeev.swipelauncher.presentation.ui.screens.settings.appListSettingsScreen.AppListSettingsScreen
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.EditCircleMenuScreenUI
import com.kindeev.swipelauncher.presentation.ui.screens.settings.launcherSettingsScreen.LauncherSettingsScreen
import com.kindeev.swipelauncher.presentation.ui.screens.settings.mainSettingsScreen.MainSettingsScreen
import com.kindeev.swipelauncher.presentation.ui.theme.LauncherTheme
import com.kindeev.swipelauncher.presentation.viewModels.settings.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.SettingsActivityVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.actionDialog.ActionDialogVM
import com.kindeev.swipelauncher.presentation.viewModels.diViewModel
import com.kindeev.swipelauncher.presentation.viewModels.onBoardingScreen.OnBoardingScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.additionalSettingsScreen.AdditionalSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.appListSettingsScreen.AppListSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.editCircleMenuScreen.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.imageDialog.ImageDialogVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.launcherSettingsScreen.LauncherSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.mainSettingsScreen.MainSettingsScreenVM

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        setContent {
            LauncherTheme {
                val settingsViewModel: SettingsActivityVM = diViewModel()
                val navigationBackStack by settingsViewModel.navigationBackStack.collectAsStateWithLifecycle()
                val dialogStrategy = remember {
                    DialogSceneStrategy<SettingsActivityNav>()
                }
                NavDisplay(
                    backStack = navigationBackStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    onBack = settingsViewModel::navigationOnBack,
                    sceneStrategies = listOf(dialogStrategy),
                    entryProvider = entryProvider {
                        entry<SettingsActivityNav.Main> {
                            val viewModel: MainSettingsScreenVM = diViewModel()
                            MainSettingsScreen(viewModel = viewModel)
                        }
                        entry<SettingsActivityNav.CircleMenus> {
                            val viewModel: AllCircleMenusScreenVM = diViewModel()
                            AllCircleMenusScreen(viewModel = viewModel)
                        }
                        entry<SettingsActivityNav.Tutorial> {
                            val viewModel: OnBoardingScreenVM = diViewModel { parameters ->
                                parameters.insert("onFinish", settingsViewModel::navigationOnBack)
                            }
                            OnboardingScreen(
                                viewModel = viewModel
                            )
                        }
                        entry<SettingsActivityNav.EditCircleMenu> { route ->
                            val viewModel: EditCircleMenuScreenVM = diViewModel(
                                insertParameters = { parameters ->
                                    parameters.insert("circleMenuId", route.circleMenuId)
                                }
                            )
                            EditCircleMenuScreenUI(viewModel = viewModel)
                        }
                        entry<SettingsActivityNav.ActionDialog>(
                            metadata = DialogSceneStrategy.dialog()
                        ) { route ->
                            val viewModel: ActionDialogVM = diViewModel()
                            ActionDialog(
                                viewModel = viewModel,
                                onDismissRequest = {
                                    settingsViewModel.navigationOnBack()
                                },
                                onPick = { action ->
                                    route.onPick(action)
                                }
                            )
                        }
                        entry<SettingsActivityNav.ImageDialog>(
                            metadata = DialogSceneStrategy.dialog()
                        ) { route ->
                            val viewModel: ImageDialogVM = diViewModel()
                            ImageDialog(
                                viewModel = viewModel,
                                onDismissRequest = {
                                    settingsViewModel.navigationOnBack()
                                },
                                onPick = { image ->
                                    route.onPick(image)
                                }
                            )
                        }
                        entry<SettingsActivityNav.Additional> {
                            val viewModel: AdditionalSettingsScreenVM = diViewModel()
                            AdditionalSettingsScreen(viewModel = viewModel)
                        }
                        entry<SettingsActivityNav.AppList> {
                            val viewModel: AppListSettingsScreenVM = diViewModel()
                            AppListSettingsScreen(viewModel = viewModel)
                        }
                        entry<SettingsActivityNav.Launcher> {
                            val viewModel: LauncherSettingsScreenVM = diViewModel()
                            LauncherSettingsScreen(viewModel = viewModel)
                        }
                    }
                )
            }
        }
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