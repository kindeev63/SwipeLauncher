package com.kindeev.swipelauncher.di

import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SaveCircleMenuWithDebounceUseCase(
    private val dataRepository: DataRepository,
    private val scope: CoroutineScope
) {
    private var job: Job? = null

    fun save(circleMenu: CircleMenu) {
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            delay(500.milliseconds)
            dataRepository.insertCircleMenu(circleMenu)
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }

    fun isActive() = job?.isActive == true
}