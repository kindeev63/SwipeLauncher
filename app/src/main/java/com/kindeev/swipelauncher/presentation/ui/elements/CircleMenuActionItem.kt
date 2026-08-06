package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kindeev.swipelauncher.presentation.entities.ActionItemData
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.CallActionItem
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.ChangeFlashlightConditionActionItem
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.DialActionItem
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.FlashlightOffActionItem
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.FlashlightOnActionItem
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.OpenAppActionItem
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.OpenCircleMenuActionItem
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.OpenSettingsActionItem
import com.kindeev.swipelauncher.presentation.ui.elements.actionItems.OpenUrlActionItem

@Composable
fun CircleMenuActionItem(
    actionItemData: ActionItemData,
    textColor: Color,
    changeAction: () -> Unit,
    changePhoneNumber: () -> Unit,
    changeUrl: () -> Unit
) {
    when (actionItemData) {
        is ActionItemData.Call -> {
            ContactNameOrPhoneNumber(actionItemData.phoneNumber) { contactOrNumber ->
                CallActionItem(
                    contactOrNumber = contactOrNumber,
                    clickOnImage = changeAction,
                    clickOnNumber = changePhoneNumber,
                )
            }
        }
        ActionItemData.ChangeFlashlightCondition -> ChangeFlashlightConditionActionItem(
            textColor = textColor,
            onClick = changeAction
        )
        is ActionItemData.Dial -> {
            ContactNameOrPhoneNumber(actionItemData.phoneNumber) { contactOrNumber ->
                DialActionItem(
                    contactOrNumber = contactOrNumber,
                    clickOnImage = changeAction,
                    clickOnNumber = changePhoneNumber,
                )
            }
        }
        ActionItemData.FlashlightOff -> FlashlightOffActionItem(
            textColor = textColor,
            onClick = {}
        )
        ActionItemData.FlashlightOn -> FlashlightOnActionItem(
            textColor = textColor,
            onClick = {}
        )
        is ActionItemData.OpenApp -> {
            OpenAppActionItem(
                title = actionItemData.title,
                imageUri = actionItemData.imageUri,
                textColor = textColor,
                onClick = changeAction
            )
        }
        is ActionItemData.OpenCircleMenu -> {
            OpenCircleMenuActionItem(
                title = actionItemData.title,
                circleMenuItems = actionItemData.items,
                circleMenuItemSize = actionItemData.itemSize,
                textColor = textColor,
                onClick = changeAction
            )
        }
        ActionItemData.OpenSettings -> {
            OpenSettingsActionItem(
                textColor = textColor,
                onClick = changeAction
            )
        }
        is ActionItemData.OpenUrl -> {
            OpenUrlActionItem(
                url = actionItemData.url,
                clickOnImage = changeAction,
                clickOnUrl = changeUrl
            )
        }
    }
}