package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import android.content.Intent
import android.net.Uri

class DeleteAppUseCase(private val context: Context) {
    fun invoke(packageName: String) {
        val packageUri = Uri.parse("package:$packageName")
        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageUri)
        context.startActivity(uninstallIntent)
    }
}