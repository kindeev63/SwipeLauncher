package com.kindeev.swipelauncher.presentation.useCases

import android.content.Context
import android.content.Intent
import android.provider.Settings

class ShowLauncherSelectionUseCase(private val context: Context) {
    fun show() {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}