package com.kindeev.swipelauncher.presentation

import android.content.Context
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
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.FlashLightUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenSettingsUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenUrlUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.TelephoneUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.interfaces.CircleMenuImageToImageBitmap
import com.kindeev.swipelauncher.presentation.navigation.MainActivityNav
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.useCases.ActionItemDataUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuImageToImageBitmapUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuItemIndexOnCordsUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuParametersUseCase
import com.kindeev.swipelauncher.presentation.useCases.GetSystemServiceUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenAppUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenChannelUseCase
import com.kindeev.swipelauncher.presentation.viewModels.settings.actionDialog.ActionDialogVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.MainActivityVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.imageDialog.ImageDialogVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.editCircleMenuScreen.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.SettingsActivityVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.additionalSettingsScreen.AdditionalSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.appListSettingsScreen.AppListSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.launcherSettingsScreen.LauncherSettingsScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.mainSettingsScreen.MainSettingsScreenVM
import com.knomster.di.DIContainer
import com.knomster.di.DIKey
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

    private fun initMainDependencies(context: Context) {
        container.insertSingle {
            context.applicationContext
        }
        container.insertSingle { // ioScope
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
        container.insertSingle<DataRepository> {
            AppDataBase.getDataBase(
                context = container.getSingle()
            ).getRepository()
        }
    }

    private fun initCoilDependencies() {
        container.insertSingle {
            CoilLoaderManager(
                context = container.getSingle()
            )
        }
    }

    private fun initUserImagesDependencies() {
        container.insertSingle {
            UserImagesStorage(
                context = container.getSingle()
            )
        }
        container.insertSingle<com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository> {
            UserImagesRepository(
                storage = container.getSingle(),
                coilLoaderManager = container.getSingle()
            )
        }
    }

    private fun initApplicationsRepositoryDependencies() {
        val appsRepository = AppsRepository(
            context = container.getSingle()
        )
        container.insertSingle<ApplicationsManager> {
            appsRepository
        }
        container.insertSingle {
            appsRepository
        }
        container.insertSingle {
            AppsObserver(
                context = container.getSingle(),
                applicationsRepository = container.getSingle(),
                coilLoaderManager = container.getSingle(),
                dataRepository = container.getSingle(),
                checkCircleMenuUseCase = container.getSingle(),
                circleMenuStateFlowUseCase = container.getSingle(),
                userImagesRepository = container.getSingle(),
                ioScope = container.getSingle(),
                circleMenuImageToImageBitmapUseCase = container.getSingle(),
            )
        }
    }

    private fun initBackupDependencies() {
        container.insertSingle {
            ExportCircleMenusUseCase(
                userImagesRepository = container.getSingle(),
                context = container.getSingle()
            )
        }
        container.insertSingle {
            ImportCircleMenusUseCase(
                userImagesRepository = container.getSingle(),
                dataRepository = container.getSingle(),
                checkCircleMenuUseCase = container.getSingle(),
                applicationsManager = container.getSingle(),
                context = container.getSingle()
            )
        }
    }

    private fun initDomainDependencies() {
        initCircleMenuActionDependencies()
        initDomainStateFlowDependencies()
        initOtherDomainDependencies()
    }

    private fun initCircleMenuActionDependencies() {
        container.insertSingle {
            FlashLightUseCase(
                context = container.getSingle()
            )
        }
        container.insertSingle {
            OpenSettingsUseCase(
                context = container.getSingle()
            )
        }
        container.insertSingle {
            OpenUrlUseCase(
                context = container.getSingle()
            )
        }
        container.insertSingle {
            TelephoneUseCase(
                context = container.getSingle()
            )
        }
    }

    private fun initDomainStateFlowDependencies() {
        container.insertSingle {
            CircleMenuStateFlowUseCase(
                dataRepository = container.getSingle(),
                ioScope = container.getSingle()
            )
        }
        container.insertSingle {
            SettingsStateFlowUseCase(
                dataRepository = container.getSingle(),
                ioScope = container.getSingle()
            )
        }
    }

    private fun initOtherDomainDependencies() {
        container.insertSingle {
            CheckCircleMenuUseCase()
        }
        container.insertSingle {
            GetRootCircleMenuUseCase(
                context = container.getSingle()
            )
        }
        container.insertSingle {
            SaveCircleMenuWithDebounceUseCase(
                dataRepository = container.getSingle(),
                scope = container.getSingle()
            )
        }
    }

    private fun initPresentationDependencies() {
        initOtherPresentationDependencies()
        initPresentationStateFlowDependencies()
        initNavigationDependencies()
    }

    private fun initNavigationDependencies() {
        container.insertSingle<NavigationComponent<MainActivityNav>>(
            key = MAIN_NAVIGATION_COMPONENT_KEY
        ) {
            NavigationComponent(MainActivityNav.Launcher)
        }
        container.insertSingle<NavigationComponent<SettingsActivityNav>>(
            key = SETTINGS_NAVIGATION_COMPONENT_KEY
        ) {
            NavigationComponent(SettingsActivityNav.Main)
        }
    }

    private fun initOtherPresentationDependencies() {
        container.insertSingle {
            GetSystemServiceUseCase(
                context = container.getSingle()
            )
        }
        container.insertSingle {
            OpenAppUseCase(
                context = container.getSingle(),
                applicationsManager = container.getSingle(),
                openSettingsUseCase = container.getSingle()
            )
        }
        container.insertSingle {
            CircleMenuParametersUseCase(
                dataRepository = container.getSingle(),
                ioScope = container.getSingle()
            )
        }
        container.insertSingle {
            CircleMenuItemIndexOnCordsUseCase(
                dataRepository = container.getSingle(),
                ioScope = container.getSingle()
            )
        }
        container.insertSingle {
            ActionItemDataUseCase(
                applicationsManager = container.getSingle(),
                circleMenuStateFlowUseCase = container.getSingle(),
                circleMenuParametersUseCase = container.getSingle(),
                circleMenuImageToImageBitmapUseCase = container.getSingle()
            )
        }
        container.insertSingle {
            OpenChannelUseCase(
                context = container.getSingle()
            )
        }
    }

    private fun initPresentationStateFlowDependencies() {
        val circleMenuImageToImageBitmap =
            CircleMenuImageToImageBitmapUseCase(
                userImagesRepository = container.getSingle(),
                context = container.getSingle(),
                ioScope = container.getSingle(),
                dataRepository = container.getSingle(),
                getSystemServiceUseCase = container.getSingle()
            )
        container.insertSingle {
            circleMenuImageToImageBitmap
        }
        container.insertSingle<CircleMenuImageToImageBitmap> {
            circleMenuImageToImageBitmap
        }
    }

    private fun initViewModels() {
        container.registerViewModel {
            AllCircleMenusScreenVM(
                dataRepository = container.getSingle(),
                exportCircleMenusUseCase = container.getSingle(),
                importCircleMenusUseCase = container.getSingle(),
                circleMenuStateFlowUseCase = container.getSingle(),
                circleMenuParametersUseCase = container.getSingle(),
                circleMenuImageToImageBitmap = container.getSingle(),
                navigationComponent = container.getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY)
            )
        }
        container.registerViewModel { parameters ->
            EditCircleMenuScreenVM(
                circleMenuId = parameters.get("circleMenuId"),
                circleMenuStateFlowUseCase = container.getSingle(),
                saveCircleMenuWithDebounceUseCase = container.getSingle(),
                settingsStateFlowUseCase = container.getSingle(),
                density = container.getSingle<Context>().resources.displayMetrics.density,
                circleMenuParametersUseCase = container.getSingle(),
                circleMenuImageToImageBitmapUseCase = container.getSingle(),
                circleMenuItemIndexOnCordsUseCase = container.getSingle(),
                navigationComponent = container.getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                actionItemDataUseCase = container.getSingle(),
            )
        }
        container.registerViewModel {
            LauncherScreenVM(
                telephoneUseCase = container.getSingle(),
                openSettingsUseCase = container.getSingle(),
                flashLightUseCase = container.getSingle(),
                openUrlUseCase = container.getSingle(),
                density = container.getSingle<Context>().resources.displayMetrics.density,
                settingsStateFlowUseCase = container.getSingle(),
                openAppUseCase = container.getSingle(),
                getSystemServiceUseCase = container.getSingle(),
                circleMenuStateFlowUseCase = container.getSingle(),
                applicationsManager = container.getSingle(),
                circleMenuImageToImageBitmap = container.getSingle(),
                circleMenuItemIndexOnCordsUseCase = container.getSingle(),
                circleMenuParametersUseCase = container.getSingle()
            )
        }
        container.registerViewModel {
            ActionDialogVM(
                circleMenuParametersUseCase = container.getSingle(),
                applicationsManager = container.getSingle(),
                circleMenuStateFlowUseCase = container.getSingle(),
                circleMenuImageToImageBitmap = container.getSingle()
            )
        }
        container.registerViewModel {
            ImageDialogVM(
                userImagesRepository = container.getSingle(),
                applicationsManager = container.getSingle()
            )
        }
        container.registerViewModel {
            MainActivityVM(
                navigationComponent = container.getSingle(key = MAIN_NAVIGATION_COMPONENT_KEY),
                getRootCircleMenuUseCase = container.getSingle(),
                dataRepository = container.getSingle(),
                context = container.getSingle()
            )
        }
        container.registerViewModel {
            SettingsActivityVM(
                navigationComponent = container.getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY)
            )
        }
        container.registerViewModel {
            AdditionalSettingsScreenVM(
                navigationComponent = container.getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                settingsStateFlowUseCase = container.getSingle(),
                dataRepository = container.getSingle(),
                context = container.getSingle()
            )
        }
        container.registerViewModel {
            AppListSettingsScreenVM(
                navigationComponent = container.getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                settingsStateFlowUseCase = container.getSingle(),
                dataRepository = container.getSingle(),
                context = container.getSingle()
            )
        }
        container.registerViewModel {
            LauncherSettingsScreenVM(
                navigationComponent = container.getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                dataRepository = container.getSingle(),
                settingsStateFlowUseCase = container.getSingle(),
                actionItemDataUseCase = container.getSingle(),
                context = container.getSingle()
            )
        }
        container.registerViewModel {
            MainSettingsScreenVM(
                navigationComponent = container.getSingle(key = SETTINGS_NAVIGATION_COMPONENT_KEY),
                openChannelUseCase = container.getSingle(),
                context = container.getSingle()
            )
        }
    }
}