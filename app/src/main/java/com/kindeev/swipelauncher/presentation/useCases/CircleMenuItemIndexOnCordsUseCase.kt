package com.kindeev.swipelauncher.presentation.useCases

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.ItemsCount
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.getAngles
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.itemIndexOnCordsGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CircleMenuItemIndexOnCordsUseCase(
    dataRepository: DataRepository,
    ioScope: CoroutineScope
) {
    private var generators = emptyMap<ItemsCount, (Offset) -> Int>()

    fun getItemIndexOnCordsGenerator(itemsCount: ItemsCount):  (Offset) -> Int {
        return generators.getOrElse(itemsCount) {
            val newGenerator = itemIndexOnCordsGenerator(getAngles(itemsCount))
            generators += itemsCount to newGenerator
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
                    generators = itemsCounts.associateWith { itemsCounts -> itemIndexOnCordsGenerator(getAngles(itemsCounts)) }
                }
        }
    }
}