package com.knomster.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class DIContainer {

    @PublishedApi
    internal val singles = ConcurrentHashMap<KClass<*>, Any>()

    @PublishedApi
    internal val viewModelCreators = ConcurrentHashMap<KClass<out ViewModel>, (SavedStateHandle) -> ViewModel>()

    inline fun <reified T: Any> insertSingle(single: T) {
        singles[T::class] = single
    }

    inline fun <reified T: Any> insertSingleAs(single: T) {
        singles[T::class] = single
    }

    inline fun <reified T> getSingle(): T {
        return singles[T::class] as? T ?: throw IllegalStateException("Not found single for ${T::class}")
    }

    inline fun <reified T: ViewModel> registerViewModel(noinline creator: (SavedStateHandle) -> T) {
        viewModelCreators[T::class] = creator
    }

    inline fun <reified T: ViewModel> getViewModelCreator(): (SavedStateHandle) -> T {
        @Suppress("UNCHECKED_CAST")
        return (viewModelCreators[T::class] as? (SavedStateHandle) -> T)
            ?: throw IllegalStateException("Not found creator for ${T::class}")
    }
}