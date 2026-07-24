package com.kindeev.swipelauncher.presentation.useCases.stateFlows

import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuForUIMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CircleMenuForUIStateFlowUseCase(
    circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    circleMenuForUIMapper: CircleMenuForUIMapper,
    ioScope: CoroutineScope
) {
    val circleMenusForUI = circleMenuStateFlowUseCase.circleMenus.map {
        it.map { menu -> circleMenuForUIMapper.map(menu) }
    }.stateIn(
        scope = ioScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )
}