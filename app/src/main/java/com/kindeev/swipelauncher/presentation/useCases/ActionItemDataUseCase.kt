package com.kindeev.swipelauncher.presentation.useCases

import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.data.coil.appImageUri
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CallAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.DialAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOffAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOnAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenUrlAction
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.ActionItemData
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemToDraw

class ActionItemDataUseCase(
    private val applicationsManager: ApplicationsManager,
    private val circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    private val circleMenuParametersUseCase: CircleMenuParametersUseCase,
    private val circleMenuImageToImageBitmapUseCase: CircleMenuImageToImageBitmapUseCase
) {
    fun getActionItem(action: CircleMenuAction): ActionItemData? = when (action) {
        is CallAction -> ActionItemData.Call(action.phoneNumber)
        ChangeFlashLightConditionAction -> ActionItemData.ChangeFlashlightCondition
        is DialAction -> ActionItemData.Dial(action.phoneNumber)
        FlashLightOffAction -> ActionItemData.FlashlightOff
        FlashLightOnAction -> ActionItemData.FlashlightOn
        is OpenAppAction -> applicationsManager.getApplication(action.packageName)?.let { app ->
            ActionItemData.OpenApp(
                title = app.title,
                imageUri = appImageUri(app.packageName)
            )
        }

        is OpenCircleMenuAction -> circleMenuStateFlowUseCase.circleMenus.value.find { it.id == action.id }
            ?.let { menu ->
                val parameters =
                    circleMenuParametersUseCase.getParametersGenerator(menu.items.size)(50f)
                ActionItemData.OpenCircleMenu(
                    title = menu.title,
                    items = menu.items.mapIndexed { index, item ->
                        parameters.offsets[index]?.let { offset ->
                            circleMenuImageToImageBitmapUseCase.getImageBitmap(item.image)
                                ?.let { imageBitmap ->
                                    CircleMenuItemToDraw(
                                        offset = offset,
                                        imageBitmap = imageBitmap
                                    )
                                }
                        }
                    }.filterNotNull(),
                    itemSize = parameters.itemSize
                )
            }

        OpenSettingsAction -> ActionItemData.OpenSettings
        is OpenUrlAction -> ActionItemData.OpenUrl(action.url)
    }
}