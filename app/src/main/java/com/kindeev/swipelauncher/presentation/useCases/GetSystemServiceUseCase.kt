package com.kindeev.swipelauncher.presentation.useCases

import android.content.Context

class GetSystemServiceUseCase(private val context: Context) {
    fun get(service: String): Any = context.getSystemService(service)
}