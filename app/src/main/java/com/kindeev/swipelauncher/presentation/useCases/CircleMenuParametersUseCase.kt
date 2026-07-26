package com.kindeev.swipelauncher.presentation.useCases

import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDrawParameters
import com.kindeev.swipelauncher.presentation.entities.MenuSize
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.ItemsCount
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.makeCircleMenuParametersGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CircleMenuParametersUseCase(
    dataRepository: DataRepository,
    ioScope: CoroutineScope
) {
    private var parameters = emptyMap<ItemsCount, (MenuSize) -> CircleMenuToDrawParameters>()

    fun getParametersGenerator(itemsCount: ItemsCount): (MenuSize) -> CircleMenuToDrawParameters {
        return parameters.getOrElse(itemsCount) {
            val newGenerator = makeCircleMenuParametersGenerator(itemsCount)
            parameters += itemsCount to newGenerator
            newGenerator
        }
    }

    init {
        ioScope.launch {
            dataRepository
                .getAllCircleMenus()
                .map { it.map { menu -> menu.items.size }.toSet() }
                .distinctUntilChanged()
                .collect { itemsCounts ->
                    parameters =
                        itemsCounts.associateWith { itemsCount -> makeCircleMenuParametersGenerator(itemsCount) }
                }
        }
    }
}