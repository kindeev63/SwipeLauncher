package com.knomster.di

interface GetParameters {
    fun <T> get(key: Any?): T
}