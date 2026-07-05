package com.kindeev.swipelauncher.domain.useCases.circleMenuActions

import android.content.Context
import android.content.Intent
import com.kindeev.swipelauncher.presentation.activities.SettingsActivity

class OpenSettingsUseCase(private val context: Context) {
    fun invoke() {
        val intent = Intent(
            context,
            SettingsActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}