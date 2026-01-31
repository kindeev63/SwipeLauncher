package com.kindeev.swipelauncher.domain.useCases.newi

import com.kindeev.swipelauncher.domain.database.CircleMenuRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CircleMenuDataState(
    circleMenuRepository: CircleMenuRepository
) {
    private val _circleMenuData = circleMenuRepository.getAllFlow().stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    operator fun invoke() = _circleMenuData
}