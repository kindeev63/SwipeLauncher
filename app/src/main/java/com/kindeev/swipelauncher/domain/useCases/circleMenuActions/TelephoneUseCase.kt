package com.kindeev.swipelauncher.domain.useCases.circleMenuActions

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

class TelephoneUseCase(private val context: Context) {
    fun call(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = "tel:$phoneNumber".toUri()
        context.startActivity(intent)
    }

    fun dial(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = "tel:$phoneNumber".toUri()
        context.startActivity(intent)
    }
}