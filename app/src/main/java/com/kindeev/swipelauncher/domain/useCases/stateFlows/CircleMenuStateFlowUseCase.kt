package com.kindeev.swipelauncher.domain.useCases.stateFlows

import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CircleMenuStateFlowUseCase(
    dataRepository: DataRepository,
    ioScope: CoroutineScope
) {
    val circleMenus = dataRepository.getAllCircleMenus().stateIn(
        scope = ioScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )
}