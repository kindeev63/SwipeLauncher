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
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuForUIMapper
import com.kindeev.swipelauncher.presentation.useCases.GetSystemServiceUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenAppUseCase
import com.kindeev.swipelauncher.presentation.useCases.stateFlows.CircleMenuForUIStateFlowUseCase
import com.kindeev.swipelauncher.presentation.viewModels.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.editCircleMenuScreen.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.MainSettingsScreenVM
import com.knomster.di.DIContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DI {
    val container = DIContainer()


    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(context: Context) {
        val appContext = context.applicationContext
        initSingles(appContext)
        initViewModels(appContext)
    }

    private fun initSingles(context: Context) {
        initDataDependencies(context)
        initDomainDependencies(context)
        initPresentationDependencies(context)
    }

    private fun initDataDependencies(context: Context) {
        initDatabaseDependencies(context)
        initCoilDependencies(context)
        initUserImagesDependencies(context)
        initBackupDependencies(context)
        initApplicationsRepositoryDependencies(context)
    }

    private fun initDatabaseDependencies(context: Context) {
        container.insertDependency<DataRepository> {
            AppDataBase.getDataBase(context).getRepository()
        }
    }

    private fun initCoilDependencies(context: Context) {
        container.insertDependency {
            CoilLoaderManager(context)
        }
    }

    private fun initUserImagesDependencies(context: Context) {
        container.insertDependency {
            UserImagesStorage(context)
        }
        container.insertDependency<com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository> {
            UserImagesRepository(
                storage = container.getDependency(),
                coilLoaderManager = container.getDependency()
            )
        }
    }

    private fun initApplicationsRepositoryDependencies(context: Context) {
        val appsRepository = AppsRepository(context)
        container.insertDependency<ApplicationsManager> {
            appsRepository
        }
        container.insertDependency {
            appsRepository
        }
        container.insertDependency {
            AppsObserver(
                context = context,
                applicationsRepository = container.getDependency(),
                coilLoaderManager = container.getDependency(),
                dataRepository = container.getDependency(),
                checkCircleMenuUseCase = container.getDependency(),
                circleMenuStateFlowUseCase = container.getDependency(),
                userImagesRepository = container.getDependency(),
                ioScope = ioScope,
            )
        }
    }

    private fun initBackupDependencies(context: Context) {
        container.insertDependency {
            ExportCircleMenusUseCase(
                userImagesRepository = container.getDependency(),
                context = context
            )
        }
        container.insertDependency {
            ImportCircleMenusUseCase(
                userImagesRepository = container.getDependency(),
                dataRepository = container.getDependency(),
                checkCircleMenuUseCase = container.getDependency(),
                applicationsManager = container.getDependency(),
                context = context
            )
        }
    }

    private fun initDomainDependencies(context: Context) {
        initCircleMenuActionDependencies(context)
        initDomainStateFlowDependencies()
        initOtherDomainDependencies(context)
    }

    private fun initCircleMenuActionDependencies(context: Context) {
        container.insertDependency {
            FlashLightUseCase(context)
        }
        container.insertDependency {
            OpenSettingsUseCase(context)
        }
        container.insertDependency {
            OpenUrlUseCase(context)
        }
        container.insertDependency {
            TelephoneUseCase(context)
        }
    }

    private fun initDomainStateFlowDependencies() {
        container.insertDependency {
            CircleMenuStateFlowUseCase(
                dataRepository = container.getDependency(),
                ioScope = ioScope
            )
        }
        container.insertDependency {
            SettingsStateFlowUseCase(
                dataRepository = container.getDependency(),
                ioScope = ioScope
            )
        }
    }

    private fun initOtherDomainDependencies(context: Context) {
        container.insertDependency {
            CheckCircleMenuUseCase()
        }
        container.insertDependency {
            GetRootCircleMenuUseCase(context)
        }
        container.insertDependency {
            SaveCircleMenuWithDebounceUseCase(
                dataRepository = container.getDependency(),
                scope = ioScope
            )
        }
    }

    private fun initPresentationDependencies(context: Context) {
        initOtherPresentationDependencies(context)
        initPresentationStateFlowDependencies()
    }

    private fun initOtherPresentationDependencies(context: Context) {
        container.insertDependency {
            CircleMenuForUIMapper(
                userImagesRepository = container.getDependency(),
                context = context
            )
        }
        container.insertDependency {
            GetSystemServiceUseCase(context)
        }
        container.insertDependency {
            OpenAppUseCase(
                context = context,
                applicationsManager = container.getDependency(),
                openSettingsUseCase = container.getDependency()
            )
        }
    }

    private fun initPresentationStateFlowDependencies() {
        container.insertDependency {
            CircleMenuForUIStateFlowUseCase(
                circleMenuStateFlowUseCase = container.getDependency(),
                circleMenuForUIMapper = container.getDependency(),
                ioScope = ioScope
            )
        }
    }

    private fun initViewModels(context: Context) {
        container.registerViewModel {
            AllCircleMenusScreenVM(
                dataRepository = container.getDependency(),
                exportCircleMenusUseCase = container.getDependency(),
                importCircleMenusUseCase = container.getDependency(),
                circleMenuStateFlowUseCase = container.getDependency(),
                circleMenuForUIStateFlowUseCase = container.getDependency()
            )
        }
        container.registerViewModel { savedStateHandle ->
            EditCircleMenuScreenVM(
                circleMenuId = savedStateHandle["circleMenuId"],
                circleMenuStateFlowUseCase = container.getDependency(),
                saveCircleMenuWithDebounceUseCase = container.getDependency(),
                userImagesRepository = container.getDependency(),
                applicationsManager = container.getDependency(),
                settingsStateFlowUseCase = container.getDependency(),
                circleMenuForUIMapper = container.getDependency(),
                density = context.resources.displayMetrics.density,
            )
        }
        container.registerViewModel {
            LauncherScreenVM(
                telephoneUseCase = container.getDependency(),
                openSettingsUseCase = container.getDependency(),
                flashLightUseCase = container.getDependency(),
                openUrlUseCase = container.getDependency(),
                density = context.resources.displayMetrics.density,
                settingsStateFlowUseCase = container.getDependency(),
                openAppUseCase = container.getDependency(),
                getSystemServiceUseCase = container.getDependency(),
                circleMenuForUIStateFlowUseCase = container.getDependency(),
                applicationsManager = container.getDependency()
            )
        }
        container.registerViewModel {
            MainSettingsScreenVM(
                applicationsManager = container.getDependency(),
                settingsStateFlowUseCase = container.getDependency(),
                dataRepository = container.getDependency()
            )
        }
    }
}