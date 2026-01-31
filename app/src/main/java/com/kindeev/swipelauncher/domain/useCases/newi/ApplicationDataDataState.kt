package com.kindeev.swipelauncher.domain.useCases.newi

import com.kindeev.swipelauncher.domain.database.ApplicationDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ApplicationDataDataState(
    private val applicationDataRepository: ApplicationDataRepository
) {

    private val _applicationDataData = applicationDataRepository.getAllFlow().stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun getApplicationData() = _applicationDataData
}