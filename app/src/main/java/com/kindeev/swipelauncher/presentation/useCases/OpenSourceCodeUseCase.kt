package com.kindeev.swipelauncher.presentation.useCases

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

class OpenSourceCodeUseCase(private val context: Context) {
    fun open() {
        val intent = Intent(Intent.ACTION_VIEW, "https://github.com/kindeev63/SwipeLauncher".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}