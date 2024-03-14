package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.entities.ApplicationData

class FilterAllAppsToSearchBoxUseCase(private val context: Context) {
    fun invoke(apps: List<ApplicationData>, searchText: String): List<ApplicationData> {
        val appsWithSettings = apps.toMutableList().apply {
            this.replaceAll { applicationData ->
                if (applicationData.packageName == context.packageName) {
                    applicationData.copy(name = context.resources.getString(R.string.launcher_settings))
                } else applicationData
            }
        }
        return appsWithSettings.filter {
            it.name.lowercase().contains(searchText.lowercase())
        }.sortedBy { it.name }
    }
}