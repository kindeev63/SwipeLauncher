package com.kindeev.swipelauncher.domain.viewModels.screens.mainSettingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.di.container
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo

class MainSettingsScreenVM(context: Context): ViewModel() {
    private val container = context.container

    fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return container.applicationsManager.getApplication(packageName)
    }

}