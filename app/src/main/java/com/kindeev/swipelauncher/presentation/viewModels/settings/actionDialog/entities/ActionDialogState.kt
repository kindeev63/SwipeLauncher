package com.kindeev.swipelauncher.presentation.viewModels.settings.actionDialog.entities

import androidx.compose.ui.text.input.TextFieldValue
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import com.kindeev.swipelauncher.domain.entities.actionTypes.FlashlightActionType
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategory
import com.kindeev.swipelauncher.domain.entities.actionTypes.TelephoneActionType
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDraw

sealed class ActionDialogState {
    data class PickCategory(val searchText: TextFieldValue, val actionCategories: List<ActionCategory>): ActionDialogState()

    data class OpenAppCategory(val searchText: TextFieldValue, val applications: List<ApplicationInfo>): ActionDialogState()

    data class OpenCircleMenuCategory(val searchText: TextFieldValue, val circleMenus: List<CircleMenuToDraw>): ActionDialogState()

    data class TelephoneCategory(val searchText: TextFieldValue, val telephoneActionTypes: List<TelephoneActionType>, val requestCallPermission: Boolean): ActionDialogState()

    data class EnterNumberDialog(val action: AllActionTypes, val phoneNumber: String, val requestReadContactsPermission: Boolean): ActionDialogState()

    data class FlashlightCategory(val searchText: TextFieldValue, val flashlightActionTypes: List<FlashlightActionType>): ActionDialogState()

    data class OpenUrlCategory(val url: TextFieldValue): ActionDialogState()
}