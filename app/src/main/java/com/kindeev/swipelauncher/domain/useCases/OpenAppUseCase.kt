package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import com.kindeev.swipelauncher.domain.isAppInstalled

class OpenAppUseCase(private val context: Context) {
    fun invoke(packageName: String) {
        if (context.isAppInstalled(packageName)) {
            val intent =
                context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let { context.startActivity(it) }
        }
    }
}