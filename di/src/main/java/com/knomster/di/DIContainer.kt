package com.knomster.di

import androidx.lifecycle.ViewModel
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

typealias DIKey = String

class DIContainer {

    @PublishedApi
    internal val defaultKey = null

    @PublishedApi
    internal val singles = ConcurrentHashMap<Pair<DIKey?, KClass<*>>, Any>()
    @PublishedApi
    internal val singleFactories = ConcurrentHashMap<Pair<DIKey?, KClass<*>>, () -> Any>()
    @PublishedApi
    internal val factories = ConcurrentHashMap<Pair<DIKey?, KClass<*>>, () -> Any>()
    @PublishedApi
    internal val viewModelCreators = ConcurrentHashMap<KClass<out ViewModel>, (GetParameters) -> ViewModel>()

    inline fun <reified T: Any> insertSingle(noinline factory: () -> T) {
        singleFactories[getDefaultKey(T::class)] = factory
    }

    inline fun <reified T: Any> insertSingle(key: DIKey, noinline factory: () -> T) {
        singleFactories[getKey(key, T::class)] = factory
    }

    inline fun <reified T: Any> insertFactory(noinline factory: () -> T) {
        factories[getDefaultKey(T::class)] = factory
    }

    inline fun <reified T: Any> insertFactory(key: DIKey, noinline factory: () -> T) {
        factories[getKey(key, T::class)] = factory
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: Any> getSingle(): T {
        val key = getDefaultKey(T::class)
        if (singles.containsKey(key))
            return singles[key] as T
        synchronized(this) {
            val factory = singleFactories[key] as? () -> T ?: throw IllegalStateException("Not found single for ${T::class}")
            val dependency = factory()
            singles[key] = dependency
            return dependency
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: Any> getSingle(
        key: DIKey
    ): T {
        val key = getKey(key, T::class)
        if (singles.containsKey(key))
            return singles[key] as T
        synchronized(this) {
            val factory = singleFactories[key] as? () -> T ?: throw IllegalStateException("Not found single for ${T::class}")
            val dependency = factory()
            singles[key] = dependency
            return dependency
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: Any> getFactory(): T {
        val key = getDefaultKey(T::class)
        val factory = factories[key] as? () -> T ?: throw IllegalStateException("Not found factory for ${T::class}")
        return factory()
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: Any> getFactory(key: DIKey): T {
        val key = getKey(key, T::class)
        val factory = factories[key] as? () -> T ?: throw IllegalStateException("Not found factory for ${T::class}")
        return factory()
    }

    inline fun <reified T: ViewModel> registerViewModel(noinline creator: (GetParameters) -> T) {
        viewModelCreators[T::class] = creator
    }

    inline fun <reified T: ViewModel> getViewModelCreator(): (GetParameters) -> T {
        @Suppress("UNCHECKED_CAST")
        return (viewModelCreators[T::class] as? (GetParameters) -> T)
            ?: throw IllegalStateException("Not found creator for ${T::class}")
    }

    @PublishedApi
    internal fun getDefaultKey(kclass: KClass<*>): Pair<DIKey?, KClass<*>> = defaultKey to kclass

    @PublishedApi
    internal fun getKey(key: DIKey, kclass: KClass<*>): Pair<DIKey, KClass<*>> = key to kclass
}