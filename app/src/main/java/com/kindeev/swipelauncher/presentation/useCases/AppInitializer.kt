package com.kindeev.swipelauncher.presentation.useCases

import android.content.res.Resources
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.applications.AppsObserver
import com.kindeev.swipelauncher.data.applications.AppsRepository
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.data.coil.prefetchApplicationImages
import com.kindeev.swipelauncher.data.userImages.getUsedUserImagesIds
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import com.kindeev.swipelauncher.domain.entities.actionTypes.FlashlightActionType
import com.kindeev.swipelauncher.domain.entities.actionTypes.TelephoneActionType
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategories
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategory
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.imageTypes.AllImageTypes
import com.kindeev.swipelauncher.domain.entities.imageTypes.ImageType
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.GetRootCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.presentation.interfaces.StringGetter
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
    private val stringGetter: StringGetter,
    private val resources: Resources
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
            launch {
                setConstants()
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
                    stringGetter.getString(
                        R.string.root
                    )
                )
            )
    }

    private fun setConstants() {
        Constants.minScreenLength = minOf(
            resources.configuration.screenWidthDp,
            resources.configuration.screenHeightDp
        ).toFloat()
        setActionAndImageTypes()
    }

    private fun setActionAndImageTypes() {
        Constants.actionCategories = listOf(
            ActionCategory(
                name = stringGetter.getString(R.string.open_app_action),
                imageResId = R.drawable.open_app_image,
                type = ActionCategories.OpenApp
            ),
            ActionCategory(
                name = stringGetter.getString(R.string.open_circle_menu_action),
                imageResId = R.drawable.open_circle_menu_image,
                type = ActionCategories.OpenCircleMenu
            ),
            ActionCategory(
                name = stringGetter.getString(R.string.telephone_action),
                imageResId = R.drawable.telephone_image,
                type = ActionCategories.Telephone
            ),
            ActionCategory(
                name = stringGetter.getString(R.string.flashlight_action),
                imageResId = R.drawable.flashlight_action,
                type = ActionCategories.Flashlight
            ),
            ActionCategory(
                name = stringGetter.getString(R.string.open_settings_action),
                imageResId = R.drawable.open_settings_image,
                type = ActionCategories.OpenSettings
            ),
            ActionCategory(
                name = stringGetter.getString(R.string.open_url_action),
                imageResId = R.drawable.open_url_image,
                type = ActionCategories.OpenUrl
            ),
            ActionCategory(
                name = stringGetter.getString(R.string.empty_action),
                imageResId = R.drawable.ic_empty,
                type = ActionCategories.Empty
            ),
        )
        Constants.flashlightActionTypes = listOf(

            FlashlightActionType(
                name = stringGetter.getString(R.string.on_flashlight_action),
                imageResId = R.drawable.on_flashlight_image,
                type = AllActionTypes.FlashLightOn
            ),
            FlashlightActionType(
                name = stringGetter.getString(R.string.off_flashlight_action),
                imageResId = R.drawable.off_flashlight_image,
                type = AllActionTypes.FlashLightOff
            ),
            FlashlightActionType(
                name = stringGetter.getString(R.string.change_condition_flashlight_action),
                imageResId = R.drawable.change_condition_flashlight_image,
                type = AllActionTypes.ChangeFlashLightCondition
            ),
        )
        Constants.telephoneActionTypes = listOf(
            TelephoneActionType(
                name = stringGetter.getString(R.string.call_telephone_action),
                imageResId = R.drawable.call_telephone_image,
                type = AllActionTypes.Call
            ),
            TelephoneActionType(
                name = stringGetter.getString(R.string.dial_telephone_action),
                imageResId = R.drawable.dial_telephone_image,
                type = AllActionTypes.Dial
            )
        )
        Constants.imageTypes = listOf(
            ImageType(
                name = stringGetter.getString(R.string.app_image),
                imageResId = R.drawable.app_image,
                type = AllImageTypes.AppImage
            ),
            ImageType(
                name = stringGetter.getString(R.string.default_image),
                imageResId = R.drawable.default_image,
                type = AllImageTypes.DefaultImage
            ),
            ImageType(
                name = stringGetter.getString(R.string.user_image),
                imageResId = R.drawable.user_image,
                type = AllImageTypes.UserImage
            ),
            ImageType(
                name = stringGetter.getString(R.string.empty_image),
                imageResId = R.drawable.ic_empty,
                type = AllImageTypes.Empty
            ),
        )
    }
}
