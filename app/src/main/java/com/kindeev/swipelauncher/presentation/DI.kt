package com.kindeev.swipelauncher.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.data.applications.AppsObserver
import com.kindeev.swipelauncher.data.applications.AppsRepository
import com.kindeev.swipelauncher.data.backup.ExportCircleMenusUseCase
import com.kindeev.swipelauncher.data.backup.ImportCircleMenusUseCase
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.data.database.AppDataBase
import com.kindeev.swipelauncher.data.database.getRepository
import com.kindeev.swipelauncher.data.userImages.UserImagesRepository
import com.kindeev.swipelauncher.data.userImages.UserImagesStorage
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.GetRootCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.SaveCircleMenuWithDebounceUseCase
import com.kindeev.swipelauncher.domain.useCases.VibrateUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.FlashLightUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenSettingsUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenUrlUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.TelephoneUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.interfaces.CircleMenuImageToImageBitmap
import com.kindeev.swipelauncher.presentation.interfaces.DrawableGetter
import com.kindeev.swipelauncher.presentation.interfaces.StringGetter
import com.kindeev.swipelauncher.presentation.navigation.MainActivityNav
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.useCases.ActionItemDataUseCase
import com.kindeev.swipelauncher.presentation.useCases.AppInitializer
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuImageToImageBitmapUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuItemIndexOnCordsUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuParametersUseCase
import com.kindeev.swipelauncher.presentation.useCases.DonationUseCase
import com.kindeev.swipelauncher.presentation.useCases.GetSystemServiceUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenAppUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenChannelUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenSourceCodeUseCase
import com.kindeev.swipelauncher.presentation.useCases.ResourcesGetter
import com.kindeev.swipelauncher.presentation.useCases.ShowLauncherSelectionUseCase
import com.kindeev.swipelauncher.presentation.viewModels.settings.actionDialog.ActionDialogVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.MainActivityVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.imageDialog.ImageDialogVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.editCircleMenuScreen.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.onBoardingScreen.OnBoardingScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.SettingsActivityVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.additionalSettingsScreen.AdditionalSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.appListSettingsScreen.AppListSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.launcherSettingsScreen.LauncherSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.mainSettingsScreen.MainSettingsScreenVM
import com.knomster.di.DIContainer
import com.knomster.di.DIKey
import com.knomster.di.GetParameters
import com.knomster.navigation_component.NavigationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DI {
    val container = DIContainer()

    const val MAIN_NAVIGATION_COMPONENT_KEY: DIKey = "main"
    const val SETTINGS_NAVIGATION_COMPONENT_KEY: DIKey = "settings"


    fun init(context: Context) {
        initMainDependencies(context)
        initDependencies()
        initViewModels()
    }

    inline fun <reified T : Any> getSingle() = container.getSingle<T>()

    inline fun <reified T : Any> getSingle(key: DIKey) = container.getSingle<T>(key)

    inline fun <reified T : Any> getFactory() = container.getFactory<T>()

    inline fun <reified T : ViewModel> getViewModelCreator() =
        container.getViewModelCreator<T>()

    private inline fun <reified T : Any> insertSingle(noinline factory: () -> T) =
        container.insertSingle(factory)

    private inline fun <reified T : Any> insertSingle(key: DIKey, noinline factory: () -> T) =
        container.insertSingle(key, factory)

    private inline fun <reified T : Any> insertFactory(noinline factory: () -> T) =
        container.insertFactory(factory)

    private inline fun <reified T : ViewModel> registerViewModel(noinline factory: (GetParameters) -> T) =
        container.registerViewModel(factory)

    private fun initMainDependencies(context: Context) {
        insertSingle {
            context.applicationContext
        }
        insertSingle { // ioScope
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }

    private fun initDependencies() {
        initDataDependencies()
        initDomainDependencies()
        initPresentationDependencies()
    }

    private fun initDataDependencies() {
        initDatabaseDependencies()
        initCoilDependencies()
        initUserImagesDependencies()
        initBackupDependencies()
        initApplicationsRepositoryDependencies()
    }

    private fun initDatabaseDependencies() {
        insertSingle<DataRepository> {
            AppDataBase.getDataBase(
                context = getSingle()
            ).getRepository()
        }
    }

    private fun initCoilDependencies() {
        insertSingle {
            CoilLoaderManager(
                context = getSingle()
            )
        }
    }

    private fun initUserImagesDependencies() {
        insertSingle {
            UserImagesStorage(
                context = getSingle()
            )
        }
        insertSingle<com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository> {
            UserImagesRepository(
                storage = getSingle(),
                coilLoaderManager = getSingle()
            )
        }
    }

    private fun initApplicationsRepositoryDependencies() {
        insertSingle {
            AppsRepository(
                context = getSingle()
            )
        }
        insertSingle<ApplicationsManager> {
            getSingle<AppsRepository>()
        }
        insertSingle {
            AppsObserver(
                context = getSingle(),
                applicationsRepository = getSingle(),
                coilLoaderManager = getSingle(),
                dataRepository = getSingle(),
                checkCircleMenuUseCase = getSingle(),
                circleMenuStateFlowUseCase = getSingle(),
                userImagesRepository = getSingle(),
                ioScope = getSingle(),
                circleMenuImageToImageBitmapUseCase = getSingle(),
            )
        }
    }

    private fun initBackupDependencies() {
        insertSingle {
            ExportCircleMenusUseCase(
                userImagesRepository = getSingle(),
                context = getSingle()
            )
        }
        insertSingle {
            ImportCircleMenusUseCase(
                userImagesRepository = getSingle(),
                dataRepository = getSingle(),
                checkCircleMenuUseCase = getSingle(),
                applicationsManager = getSingle(),
                context = getSingle()
            )
        }
    }

    private fun initDomainDependencies() {
        initCircleMenuActionDependencies()
        initDomainStateFlowDependencies()
        initOtherDomainDependencies()
    }

    private fun initCircleMenuActionDependencies() {
        insertSingle {
            FlashLightUseCase(
                context = getSingle()
            )
        }
        insertSingle {
            OpenSettingsUseCase(
                context = getSingle()
            )
        }
        insertSingle {
            OpenUrlUseCase(
                context = getSingle()
            )
        }
        insertSingle {
            TelephoneUseCase(
                context = getSingle()
            )
        }
    }

    private fun initDomainStateFlowDependencies() {
        insertSingle {
            CircleMenuStateFlowUseCase(
                dataRepository = getSingle(),
                ioScope = getSingle()
            )
        }
        insertSingle {
            SettingsStateFlowUseCase(
                dataRepository = getSingle(),
                ioScope = getSingle()
            )
        }
    }

    private fun initOtherDomainDependencies() {
        insertSingle {
            CheckCircleMenuUseCase()
        }
        insertSingle {
            GetRootCircleMenuUseCase(
                context = getSingle()
            )
        }
        insertSingle {
            SaveCircleMenuWithDebounceUseCase(
                dataRepository = getSingle(),
                scope = getSingle()
            )
        }
        insertSingle {
            VibrateUseCase(
                context = getSingle()
            )
        }
    }

    private fun initPresentationDependencies() {
        initPresentationStateFlowDependencies()
        initNavigationDependencies()
        initAppInitializerDependency()
        initResourcesDependencies()
        initOtherPresentationDependencies()
    }

    private fun initResourcesDependencies() {
        insertSingle {
            ResourcesGetter(
                context = getSingle()
            )
        }
        insertSingle<StringGetter> {
            getSingle<ResourcesGetter>()
        }
        insertSingle<DrawableGetter> {
            getSingle<ResourcesGetter>()
        }
    }

    private fun initAppInitializerDependency() {
        insertFactory {
            AppInitializer(
                checkCircleMenuUseCase = getSingle(),
                circleMenuStateFlowUseCase = getSingle(),
                appsRepository = getSingle(),
                userImagesRepository = getSingle(),
                ioScope = getSingle(),
                coilLoaderManager = getSingle(),
                appsObserver = getSingle(),
                dataRepository = getSingle(),
                getRootCircleMenuUseCase = getSingle(),
                stringGetter = getSingle(),
                resources = getSingle<Context>().resources
            )
        }
    }

    private fun initNavigationDependencies() {
        insertSingle<NavigationComponent<MainActivityNav>>(
            key = MAIN_NAVIGATION_COMPONENT_KEY
        ) {
            NavigationComponent(MainActivityNav.Launcher)
        }
        insertSingle<NavigationComponent<SettingsActivityNav>>(
            key = SETTINGS_NAVIGATION_COMPONENT_KEY
        ) {
            NavigationComponent(SettingsActivityNav.Main)
        }
    }

    private fun initOtherPresentationDependencies() {
        insertSingle {
            GetSystemServiceUseCase(
                context = getSingle()
            )
        }
        insertSingle {
            OpenAppUseCase(
                context = getSingle(),
                applicationsManager = getSingle(),
                openSettingsUseCase = getSingle()
            )
        }
        insertSingle {
            CircleMenuParametersUseCase(
                dataRepository = getSingle(),
                ioScope = getSingle()
            )
        }
        insertSingle {
            CircleMenuItemIndexOnCordsUseCase(
                dataRepository = getSingle(),
                ioScope = getSingle()
            )
        }
        insertSingle {
            ActionItemDataUseCase(
                applicationsManager = getSingle(),
                circleMenuStateFlowUseCase = getSingle(),
                circleMenuParametersUseCase = getSingle(),
                circleMenuImageToImageBitmapUseCase = getSingle()
            )
        }
        insertSingle {
            OpenChannelUseCase(
                context = getSingle()
            )
        }
        insertSingle {
            OpenSourceCodeUseCase(
                context = getSingle()
            )
        }
        insertSingle {
            DonationUseCase(
                context = getSingle()
            )
        }
        insertSingle {
            ShowLauncherSelectionUseCase(
                context = getSingle()
            )
        }
    }

    private fun initPresentationStateFlowDependencies() {
        insertSingle {
            CircleMenuImageToImageBitmapUseCase(
                userImagesRepository = getSingle(),
                context = getSingle(),
                ioScope = getSingle(),
                dataRepository = getSingle(),
                getSystemServiceUseCase = getSingle(),
                drawableGetter = getSingle()
            )
        }
        insertSingle<CircleMenuImageToImageBitmap> {
            getSingle<CircleMenuImageToImageBitmapUseCase>()
        }
    }

    private fun initViewModels() {
        registerViewModel {
            AllCircleMenusScreenVM(
                dataRepository = getSingle(),
                exportCircleMenusUseCase = getSingle(),
                importCircleMenusUseCase = getSingle(),
                circleMenuStateFlowUseCase = getSingle(),
                circleMenuParametersUseCase = getSingle(),
                circleMenuImageToImageBitmap = getSingle(),
                navigationComponent = getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY)
            )
        }
        registerViewModel { parameters ->
            EditCircleMenuScreenVM(
                circleMenuId = parameters.get("circleMenuId"),
                circleMenuStateFlowUseCase = getSingle(),
                saveCircleMenuWithDebounceUseCase = getSingle(),
                settingsStateFlowUseCase = getSingle(),
                density = getSingle<Context>().resources.displayMetrics.density,
                circleMenuParametersUseCase = getSingle(),
                circleMenuImageToImageBitmapUseCase = getSingle(),
                circleMenuItemIndexOnCordsUseCase = getSingle(),
                navigationComponent = getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                actionItemDataUseCase = getSingle(),
                drawableGetter = getSingle(),
            )
        }
        registerViewModel {
            LauncherScreenVM(
                telephoneUseCase = getSingle(),
                openSettingsUseCase = getSingle(),
                flashLightUseCase = getSingle(),
                openUrlUseCase = getSingle(),
                density = getSingle<Context>().resources.displayMetrics.density,
                settingsStateFlowUseCase = getSingle(),
                openAppUseCase = getSingle(),
                vibrateUseCase = getSingle(),
                circleMenuStateFlowUseCase = getSingle(),
                applicationsManager = getSingle(),
                circleMenuImageToImageBitmap = getSingle(),
                circleMenuItemIndexOnCordsUseCase = getSingle(),
                circleMenuParametersUseCase = getSingle()
            )
        }
        registerViewModel {
            ActionDialogVM(
                circleMenuParametersUseCase = getSingle(),
                applicationsManager = getSingle(),
                circleMenuStateFlowUseCase = getSingle(),
                circleMenuImageToImageBitmap = getSingle()
            )
        }
        registerViewModel {
            ImageDialogVM(
                userImagesRepository = getSingle(),
                applicationsManager = getSingle()
            )
        }
        registerViewModel {
            MainActivityVM(
                navigationComponent = getSingle(key = MAIN_NAVIGATION_COMPONENT_KEY),
                context = getSingle()
            )
        }
        registerViewModel {
            SettingsActivityVM(
                navigationComponent = getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY)
            )
        }
        registerViewModel {
            AdditionalSettingsScreenVM(
                navigationComponent = getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                settingsStateFlowUseCase = getSingle(),
                dataRepository = getSingle(),
                stringGetter = getSingle(),
            )
        }
        registerViewModel {
            AppListSettingsScreenVM(
                navigationComponent = getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                settingsStateFlowUseCase = getSingle(),
                dataRepository = getSingle(),
                context = getSingle()
            )
        }
        registerViewModel {
            LauncherSettingsScreenVM(
                navigationComponent = getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                dataRepository = getSingle(),
                settingsStateFlowUseCase = getSingle(),
                actionItemDataUseCase = getSingle(),
                stringGetter = getSingle()
            )
        }
        registerViewModel {
            MainSettingsScreenVM(
                navigationComponent = getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                openChannelUseCase = getSingle(),
                openSourceCodeUseCase = getSingle(),
                donationUseCase = getSingle(),
                showLauncherSelectionUseCase = getSingle(),
                stringGetter = getSingle()
            )
        }
        registerViewModel { parameters ->
            OnBoardingScreenVM(
                showLauncherSelectionUseCase = getSingle(),
                stringGetter = getSingle(),
                onFinish = parameters.get("onFinish")
            )
        }
    }
}