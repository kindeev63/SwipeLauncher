package com.knomster.di

class DIParameters: InsertParameters, GetParameters {
    private val parameters = mutableMapOf<Any?, Any?>()

    override fun insert(key: Any?, value: Any?) {
        parameters[key] = value
    }

    override fun <T> get(key: Any?): T {
        @Suppress("UNCHECKED_CAST")
        return parameters[key] as T
    }
}