package com.knomster.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class DIContainer {

    @PublishedApi
    internal val singles = ConcurrentHashMap<KClass<*>, Any>()
    @PublishedApi
    internal val singleFactories = ConcurrentHashMap<KClass<*>, () -> Any>()
    @PublishedApi
    internal val viewModelCreators = ConcurrentHashMap<KClass<out ViewModel>, (SavedStateHandle) -> ViewModel>()

    inline fun <reified T: Any> insertSingle(noinline factory: () -> T) {
        singleFactories[T::class] = factory
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: Any> getSingle(): T {
        val key = T::class
        if (singles.containsKey(key))
            return singles[key] as T
        synchronized(this) {
            val factory = singleFactories[key] as? () -> T ?: throw IllegalStateException("Not found single for ${T::class}")
            val dependency = factory()
            singles[key] = dependency
            return dependency
        }
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