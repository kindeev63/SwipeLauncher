package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import android.content.pm.PackageManager

class OpenAppUseCase(private val context: Context) {
    fun invoke(packageName: String) {
        val isAppInstalled = try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.packageName == packageName
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        if (isAppInstalled) {
            val intent =
                context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let { context.startActivity(it) }
        }
    }
}