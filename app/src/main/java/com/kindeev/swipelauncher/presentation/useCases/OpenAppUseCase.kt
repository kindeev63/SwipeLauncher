package com.kindeev.swipelauncher.presentation.useCases

import android.content.Context
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenSettingsUseCase

class OpenAppUseCase(
    private val context: Context,
    private val applicationsManager: ApplicationsManager,
    private val openSettingsUseCase: OpenSettingsUseCase
) {
    fun open(packageName: String) {
        if (packageName == context.packageName) {
            openSettingsUseCase()
        } else {
            applicationsManager.open(packageName)
        }
    }
}