package com.kindeev.swipelauncher.domain.useCases.stateFlows

import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SettingsStateFlowUseCase(
    dataRepository: DataRepository,
    ioScope: CoroutineScope
) {
    val settings = dataRepository.getSettings().stateIn(
        scope = ioScope,
        started = SharingStarted.Eagerly,
        initialValue = Constants.defaultSettings
    )
}