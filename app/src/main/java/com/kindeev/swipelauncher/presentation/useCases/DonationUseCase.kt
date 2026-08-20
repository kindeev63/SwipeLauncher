package com.kindeev.swipelauncher.presentation.useCases

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

class DonationUseCase(private val context: Context) {
    fun open() {
        val intent = Intent(Intent.ACTION_VIEW, "https://pay.cloudtips.ru/p/bb5cc208".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}