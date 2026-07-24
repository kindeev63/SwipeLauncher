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
        initCoil(context)
        initDatabase(context)
        initUserImages(context)
        initApplicationsRepository(context)
        initDomainUseCases(context)
        initBackupUseCases(context)
        initPresentationUseCases(context)
    }

    private fun initDatabase(context: Context) {
        container.insertSingleAs<DataRepository>(
            AppDataBase.getDataBase(context).getRepository()
        )
    }

    private fun initCoil(context: Context) {
        container.insertSingle(
            CoilLoaderManager(context)
        )
    }

    private fun initUserImages(context: Context) {
        container.insertSingle(
            UserImagesStorage(context)
        )
        container.insertSingleAs<com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository>(
            UserImagesRepository(
                storage = container.getSingle(),
                coilLoaderManager = container.getSingle()
            )
        )
    }

    private fun initApplicationsRepository(context: Context) {
        val appsRepository = AppsRepository(context)
        container.insertSingleAs<ApplicationsManager>(
            appsRepository
        )
        container.insertSingle(
            appsRepository
        )
        container.insertSingle(
            AppsObserver(
                context = context,
                applicationsRepository = container.getSingle(),
                coilLoaderManager = container.getSingle()
            )
        )
    }

    private fun initBackupUseCases(context: Context) {
        container.insertSingle(
            ExportCircleMenusUseCase(
                userImagesRepository = container.getSingle(),
                context = context
            )
        )
        container.insertSingle(
            ImportCircleMenusUseCase(
                userImagesRepository = container.getSingle(),
                dataRepository = container.getSingle(),
                checkCircleMenuUseCase = container.getSingle(),
                applicationsManager = container.getSingle(),
                context = context
            )
        )
    }

    private fun initDomainUseCases(context: Context) {
        initCircleMenuActions(context)
        initDomainStateFlows()
        initOtherDomainUseCases(context)
    }

    private fun initCircleMenuActions(context: Context) {
        container.insertSingle(FlashLightUseCase(context))
        container.insertSingle(OpenSettingsUseCase(context))
        container.insertSingle(OpenUrlUseCase(context))
        container.insertSingle(TelephoneUseCase(context))
    }

    private fun initDomainStateFlows() {
        container.insertSingle(
            CircleMenuStateFlowUseCase(
                dataRepository = container.getSingle(),
                ioScope = ioScope
            )
        )
        container.insertSingle(
            SettingsStateFlowUseCase(
                dataRepository = container.getSingle(),
                ioScope = ioScope
            )
        )
    }

    private fun initOtherDomainUseCases(context: Context) {
        container.insertSingle(
            CheckCircleMenuUseCase()
        )
        container.insertSingle(
            GetRootCircleMenuUseCase(context)
        )
        container.insertSingle(
            SaveCircleMenuWithDebounceUseCase(
                dataRepository = container.getSingle(),
                scope = ioScope
            )
        )
    }

    private fun initPresentationUseCases(context: Context) {
        initOtherPresentationUseCases(context)
        initPresentationStateFlows()
    }

    private fun initOtherPresentationUseCases(context: Context) {
        container.insertSingle(
            CircleMenuForUIMapper(
                userImagesRepository = container.getSingle(),
                context = context
            )
        )
        container.insertSingle(
            GetSystemServiceUseCase(context)
        )
        container.insertSingle(
            OpenAppUseCase(
                context = context,
                applicationsManager = container.getSingle(),
                openSettingsUseCase = container.getSingle()
            )
        )
    }

    private fun initPresentationStateFlows() {
        container.insertSingle(
            CircleMenuForUIStateFlowUseCase(
                circleMenuStateFlowUseCase = container.getSingle(),
                circleMenuForUIMapper = container.getSingle(),
                ioScope = ioScope
            ),
        )
    }

    private fun initViewModels(context: Context) {
        container.registerViewModel {
            AllCircleMenusScreenVM(
                dataRepository = container.getSingle(),
                exportCircleMenusUseCase = container.getSingle(),
                importCircleMenusUseCase = container.getSingle(),
                circleMenuStateFlowUseCase = container.getSingle(),
                circleMenuForUIStateFlowUseCase = container.getSingle()
            )
        }
        container.registerViewModel { savedStateHandle ->
            EditCircleMenuScreenVM(
                circleMenuId = savedStateHandle["circleMenuId"],
                circleMenuStateFlowUseCase = container.getSingle(),
                saveCircleMenuWithDebounceUseCase = container.getSingle(),
                userImagesRepository = container.getSingle(),
                applicationsManager = container.getSingle(),
                settingsStateFlowUseCase = container.getSingle(),
                circleMenuForUIMapper = container.getSingle(),
                density = context.resources.displayMetrics.density,
            )
        }
        container.registerViewModel {
            LauncherScreenVM(
                telephoneUseCase = container.getSingle(),
                openSettingsUseCase = container.getSingle(),
                flashLightUseCase = container.getSingle(),
                openUrlUseCase = container.getSingle(),
                density = context.resources.displayMetrics.density,
                settingsStateFlowUseCase = container.getSingle(),
                openAppUseCase = container.getSingle(),
                getSystemServiceUseCase = container.getSingle(),
                circleMenuForUIStateFlowUseCase = container.getSingle(),
                applicationsManager = container.getSingle()
            )
        }
        container.registerViewModel {
            MainSettingsScreenVM(
                applicationsManager = container.getSingle(),
                settingsStateFlowUseCase = container.getSingle(),
                dataRepository = container.getSingle()
            )
        }
    }
}