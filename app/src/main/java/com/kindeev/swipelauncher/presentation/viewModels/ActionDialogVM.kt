package com.kindeev.swipelauncher.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemToDraw
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDraw
import com.kindeev.swipelauncher.presentation.interfaces.CircleMenuImageToImageBitmap
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuParametersUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

class ActionDialogVM(
    private val circleMenuParametersUseCase: CircleMenuParametersUseCase,
    val applicationsManager: ApplicationsManager,
    circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    circleMenuImageToImageBitmap: CircleMenuImageToImageBitmap,
) : ViewModel() {

    private val menuSize = ((Constants.minScreenLength - 20f) / 3 - 6) * 2 / 3

    val circleMenus =
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
}