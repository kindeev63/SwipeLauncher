package com.kindeev.swipelauncher.data.applications

import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import kotlinx.coroutines.flow.StateFlow

interface ApplicationsManager {

    val applications: StateFlow<List<ApplicationInfo>>

    fun getApplication(packageName: String): ApplicationInfo?

    fun open(packageName: String)

    fun delete(packageName: String)

    fun openAppDetails(packageName: String)
}