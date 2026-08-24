package com.kindeev.swipelauncher.presentation.useCases

import android.content.Context
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.applications.AppsObserver
import com.kindeev.swipelauncher.data.applications.AppsRepository
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.data.coil.prefetchApplicationImages
import com.kindeev.swipelauncher.data.userImages.getUsedUserImagesIds
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.GetRootCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch

class AppInitializer(
    private val checkCircleMenuUseCase: CheckCircleMenuUseCase,
    private val circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    private val appsRepository: AppsRepository,
    private val userImagesRepository: UserImagesRepository,
    private val ioScope: CoroutineScope,
    private val coilLoaderManager: CoilLoaderManager,
    private val appsObserver: AppsObserver,
    private val dataRepository: DataRepository,
    private val getRootCircleMenuUseCase: GetRootCircleMenuUseCase,
    private val context: Context
) {
    fun initialize() {
        ioScope.launch {
            launch {
                appsRepository.loadAllApplicationsToStateFlow()
                prefetchAppsImages()
                appsObserver.start()
                updateCircleMenus()
            }
            launch {
                removeUnusedUserImagesWhenCircleMenusUpdated()
            }
            launch {
                insertRootCircleMenuIfNecessary()
            }
        }
    }

    private fun prefetchAppsImages() {
        val packageNames = appsRepository.applications.value.map { it.packageName }
        coilLoaderManager.prefetchApplicationImages(packageNames)
    }

    private suspend fun updateCircleMenus() {
        val changedCircleMenus = getChangedCircleMenus()
        if (changedCircleMenus.isNotEmpty())
            dataRepository.insertCircleMenus(changedCircleMenus)
    }

    private suspend fun getChangedCircleMenus(): List<CircleMenu> {
        return checkCircleMenuUseCase.getOnlyChanged(
            circleMenus = circleMenuStateFlowUseCase.circleMenus.value,
            allPackageNames = appsRepository.applications.value.map { it.packageName },
            userImageIds = userImagesRepository.getAllIds()
        )
    }

    private suspend fun removeUnusedUserImagesWhenCircleMenusUpdated() {
        circleMenuStateFlowUseCase.circleMenus
            .filterNotEmpty().collect { circleMenus ->
                val usedIds = circleMenus.getUsedUserImagesIds()
                userImagesRepository.removeUnused(usedIds)
            }
    }

    private fun Flow<List<CircleMenu>>.filterNotEmpty() = filterNot { it.isEmpty() }

    private suspend fun insertRootCircleMenuIfNecessary() {
        if (dataRepository.getCircleMenus().isEmpty())
            dataRepository.insertCircleMenu(
                getRootCircleMenuUseCase.get(
                    context.resources.getString(
                        R.string.root
                    )
                )
            )
    }
}
