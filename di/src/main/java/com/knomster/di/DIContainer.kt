package com.knomster.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class DIContainer {

    @PublishedApi
    internal val dependencies = ConcurrentHashMap<KClass<*>, Any>()

    @PublishedApi
    internal val dependencyFactories = ConcurrentHashMap<KClass<*>, () -> Any>()
    @PublishedApi
    internal val viewModelCreators = ConcurrentHashMap<KClass<out ViewModel>, (SavedStateHandle) -> ViewModel>()

    inline fun <reified T: Any> insertDependency(noinline factory: () -> T) {
        dependencyFactories[T::class] = factory
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: Any> getDependency(): T {
        val key = T::class
        if (dependencies.containsKey(key))
            return dependencies[key] as T
        synchronized(this) {
            val factory = dependencyFactories[key] as? () -> T ?: throw IllegalStateException("Not found dependency for ${T::class}")
            val dependency = factory()
            dependencies[key] = dependency
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