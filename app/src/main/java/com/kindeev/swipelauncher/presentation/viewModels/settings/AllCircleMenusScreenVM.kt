package com.kindeev.swipelauncher.presentation.viewModels.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.backup.ExportCircleMenusUseCase
import com.kindeev.swipelauncher.data.backup.ImportCircleMenusUseCase
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemToDraw
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDraw
import com.kindeev.swipelauncher.presentation.interfaces.CircleMenuImageToImageBitmap
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuParametersUseCase
import com.knomster.navigation_component.NavigationComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AllCircleMenusScreenVM(
    private val dataRepository: DataRepository,
    private val exportCircleMenusUseCase: ExportCircleMenusUseCase,
    private val importCircleMenusUseCase: ImportCircleMenusUseCase,
    private val circleMenuParametersUseCase: CircleMenuParametersUseCase,
    private val circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    private val navigationComponent: NavigationComponent<SettingsActivityNav>,
    circleMenuImageToImageBitmap: CircleMenuImageToImageBitmap,
) : ViewModel() {

    private val menuSize = MutableStateFlow(((Constants.minScreenLength / 2f) - 6) * 2 / 3)

    private val _showDeleteCircleMenusDialog = MutableStateFlow(false)
    val showDeleteCircleMenusDialog: StateFlow<Boolean> = _showDeleteCircleMenusDialog.asStateFlow()

    val circleMenus = combine(
        circleMenuStateFlowUseCase.circleMenus,
        circleMenuImageToImageBitmap.mapper,
        menuSize
    ) { menus, imageMapper, menuSize ->
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

    fun onBackPressed(changeStatusBar: suspend () -> Unit) {
        if (selectedMenuIds.value.isNotEmpty()) {
            finishSelect()
        } else {
            viewModelScope.launch {
                changeStatusBar()
            }
            navigationComponent.popUpBackStackSafe()
        }
    }

    fun addNewCircleMenu() {
        navigationComponent.addToBackStack(SettingsActivityNav.EditCircleMenu(null))
    }

    private val _selectedMenuIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedMenuIds: StateFlow<List<Int>> = _selectedMenuIds

    fun setScreenWidth(screenWidth: Int) {
        menuSize.value = ((screenWidth / 2f) - 6) * 2 / 3
    }

    fun selectAllMenus() {
        _selectedMenuIds.value = circleMenus.value.map { it.id }
    }

    fun deleteSelectedMenus() = viewModelScope.launch {
        dataRepository.deleteCircleMenuByIds(selectedMenuIds.value.filter { it != 0 })
        finishSelect()
        closeDeleteCircleMenusDialog()
    }

    fun exportSelectedMenus(onFinish: (Boolean) -> Unit) {
        onFinish(
            exportCircleMenusUseCase.export(
                circleMenuStateFlowUseCase.circleMenus.value.filter { selectedMenuIds.value.contains(it.id) }
            )
        )
        finishSelect()
    }

    fun importCircleMenus(uri: Uri, onFinish: (Boolean) -> Unit) = viewModelScope.launch {
        onFinish(
            importCircleMenusUseCase.import(uri)
        )
    }

    fun finishSelect() {
        _selectedMenuIds.value = emptyList()
    }

    private fun changeSelectionStateOf(id: Int) {
        _selectedMenuIds.value =
            selectedMenuIds.value.toMutableList().apply {
                if (contains(id)) {
                    remove(id)
                } else {
                    add(id)
                }
            }
    }


    fun clickOnCircleMenuItem(id: Int) {
        if (selectedMenuIds.value.isEmpty()) {
            navigationComponent.addToBackStack(SettingsActivityNav.EditCircleMenu(id))
        } else {
            changeSelectionStateOf(id)
        }
    }

    fun longClickOnCircleMenuItem(id: Int) {
        changeSelectionStateOf(id)
    }

    fun showDeleteCircleMenusDialog() {
        _showDeleteCircleMenusDialog.value = true
    }

    fun closeDeleteCircleMenusDialog() {
        _showDeleteCircleMenusDialog.value = false
    }

}