package com.kindeev.swipelauncher.domain.useCases.circleMenuActions

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

class OpenUrlUseCase(private val context: Context) {
    fun open(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }
}