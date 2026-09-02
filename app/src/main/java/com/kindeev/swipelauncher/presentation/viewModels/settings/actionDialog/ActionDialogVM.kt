package com.kindeev.swipelauncher.presentation.viewModels.settings.actionDialog

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategories
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CallAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.DialAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.EmptyAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemToDraw
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDraw
import com.kindeev.swipelauncher.presentation.interfaces.CircleMenuImageToImageBitmap
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuParametersUseCase
import com.kindeev.swipelauncher.presentation.viewModels.settings.actionDialog.entities.ActionDialogState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActionDialogVM(
    private val circleMenuParametersUseCase: CircleMenuParametersUseCase,
    applicationsManager: ApplicationsManager,
    circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    circleMenuImageToImageBitmap: CircleMenuImageToImageBitmap,
) : ViewModel() {

    private val menuSize = ((Constants.minScreenLength - 20f) / 3 - 6) * 2 / 3

    private val applications = applicationsManager.applications

    private val _state = MutableStateFlow<ActionDialogState>(
        ActionDialogState.PickCategory(
            searchText = TextFieldValue(""),
            actionCategories = Constants.actionCategories
        )
    )

    private val _pickAction = MutableSharedFlow<CircleMenuAction>()
    val pickAction: SharedFlow<CircleMenuAction> = _pickAction.asSharedFlow()

    private val _pickContact = MutableSharedFlow<Boolean>()
    val pickContact: SharedFlow<Boolean> = _pickContact.asSharedFlow()

    val state: StateFlow<ActionDialogState> = _state.asStateFlow()

    private val circleMenus =
        circleMenuStateFlowUseCase.circleMenus.combine(circleMenuImageToImageBitmap.mapper) { menus, imageMapper ->
            menus.map { menu ->
                val parameters =
                    circleMenuParametersUseCase.getParametersGenerator(menu.items.size)(menuSize)
                CircleMenuToDraw(
                    id = menu.id,
                    title = menu.title,
                    menuSize = menuSize,
                    itemSize = parameters.itemSize,
                    items = menu.items.mapIndexed { index, item ->
                        parameters.offsets[index]?.let { offset ->
                            imageMapper[item.image]?.let { imageBitmap ->
                                CircleMenuItemToDraw(
                                    offset = offset,
                                    imageBitmap = imageBitmap
                                )
                            }
                        }
                    }.filterNotNull()
                )
            }
        }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun pickCategory(category: ActionCategories) {
        when (category) {
            ActionCategories.OpenApp -> {
                _state.value = ActionDialogState.OpenAppCategory(
                    searchText = TextFieldValue(""),
                    applications = applications.value
                )
            }
            ActionCategories.OpenCircleMenu -> {
                _state.value = ActionDialogState.OpenCircleMenuCategory(
                    searchText = TextFieldValue(""),
                    circleMenus = circleMenus.value
                )
            }
            ActionCategories.Flashlight -> {
                _state.value = ActionDialogState.FlashlightCategory(
                    searchText = TextFieldValue(""),
                    flashlightActionTypes = Constants.flashlightActionTypes
                )
            }
            ActionCategories.Telephone -> {
                _state.value = ActionDialogState.TelephoneCategory(
                    searchText = TextFieldValue(""),
                    telephoneActionTypes = Constants.telephoneActionTypes,
                    requestCallPermission = false
                )
            }
            ActionCategories.OpenSettings -> {
                viewModelScope.launch {
                    _pickAction.emit(OpenSettingsAction)
                }
            }
            ActionCategories.OpenUrl -> {
                _state.value = ActionDialogState.OpenUrlCategory(
                    url = TextFieldValue()
                )
            }

            ActionCategories.Empty ->
                viewModelScope.launch {
                    _pickAction.emit(EmptyAction)
                }

        }
    }

    fun search(searchText: TextFieldValue) {
        when (val currentState = state.value) {
            is ActionDialogState.EnterNumberDialog -> {}
            is ActionDialogState.FlashlightCategory -> {
                _state.value = currentState.copy(
                    searchText = searchText,
                    flashlightActionTypes = Constants.flashlightActionTypes.filter { it.name.lowercase().contains(searchText.text.lowercase()) }
                )
            }
            is ActionDialogState.OpenAppCategory -> {
                _state.value = currentState.copy(
                    searchText = searchText,
                    applications = applications.value.filter { it.title.lowercase().contains(searchText.text.lowercase()) }
                )
            }
            is ActionDialogState.OpenCircleMenuCategory -> {
                _state.value = currentState.copy(
                    searchText = searchText,
                    circleMenus = circleMenus.value.filter { it.title.lowercase().contains(searchText.text.lowercase()) }
                )
            }
            is ActionDialogState.OpenUrlCategory -> {}
            is ActionDialogState.PickCategory -> {
                _state.value = currentState.copy(
                    searchText = searchText,
                    actionCategories = Constants.actionCategories.filter { it.name.lowercase().contains(searchText.text.lowercase()) }
                )
            }
            is ActionDialogState.TelephoneCategory -> {
                _state.value = currentState.copy(
                    searchText = searchText,
                    telephoneActionTypes = Constants.telephoneActionTypes.filter { it.name.lowercase().contains(searchText.text.lowercase()) }
                )
            }
        }
    }

    fun pickAction(action: CircleMenuAction) {
        viewModelScope.launch {
            _pickAction.emit(action)
        }
    }

    fun openPickCategory() {
        _state.value = ActionDialogState.PickCategory(
            searchText = TextFieldValue(""),
            actionCategories = Constants.actionCategories
        )
    }

    fun openEnterNumberDialog(action: AllActionTypes) {
        _state.value = ActionDialogState.EnterNumberDialog(
            action = action,
            phoneNumber = "",
            requestReadContactsPermission = false
        )
    }

    fun openCallActionWithPermission() {
        val currentState = state.value
        if (currentState is ActionDialogState.TelephoneCategory) {
            _state.value = currentState.copy(requestCallPermission = true)
        }
    }

    fun callPermissionResult(result: Boolean) {
        val currentState = state.value
        if (currentState is ActionDialogState.TelephoneCategory) {
            if (result) {
                openEnterNumberDialog(AllActionTypes.Call)
            } else {
                _state.value = currentState.copy(requestCallPermission = false)
            }
        }
    }

    fun pickTelephoneAction() {
        val currentState = state.value
        if (currentState is ActionDialogState.EnterNumberDialog) {
            viewModelScope.launch {
                _pickAction.emit(
                    when (currentState.action) {
                        AllActionTypes.Call -> CallAction(currentState.phoneNumber)
                        AllActionTypes.Dial -> DialAction(currentState.phoneNumber)
                        else -> throw IllegalStateException("Illegal telephone action type")
                    }
                )
            }
        }
    }

    fun openTelephoneActions() {
        _state.value = ActionDialogState.TelephoneCategory(
            searchText = TextFieldValue(""),
            telephoneActionTypes = Constants.telephoneActionTypes,
            requestCallPermission = false
        )
    }

    fun onChangeNumber(phoneNumber: String) {
        val currentState = state.value
        if (currentState is ActionDialogState.EnterNumberDialog) {
            _state.value = currentState.copy(phoneNumber = phoneNumber)
        }
    }

    fun pickContact() {
        val currentState = state.value
        if (currentState is ActionDialogState.EnterNumberDialog) {
            _state.value = currentState.copy(requestReadContactsPermission = true)
        }
    }

    fun onPickContact(phoneNumber: String) {
        val currentState = state.value
        if (currentState is ActionDialogState.EnterNumberDialog) {
            _state.value = currentState.copy(phoneNumber = phoneNumber)
        }
    }

    fun readContactsPermissionResult(result: Boolean) {
        val currentState = state.value
        if (currentState is ActionDialogState.EnterNumberDialog) {
            _state.value = currentState.copy(requestReadContactsPermission = false)
            if (result) {
                viewModelScope.launch {
                    _pickContact.emit(true)
                }
            }
        }
    }

    fun changeUrl(url: TextFieldValue) {
        val currentState = state.value
        if (currentState is ActionDialogState.OpenUrlCategory) {
            _state.value = currentState.copy(url = url)
        }
    }
}