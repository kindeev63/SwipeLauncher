package com.kindeev.swipelauncher.domain.useCases.circleMenuActions

import android.content.Context
import android.content.Intent
import android.net.Uri

class OpenUrlUseCase(private val context: Context) {
    fun open(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}