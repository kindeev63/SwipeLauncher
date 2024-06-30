package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.check
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.getApplications
import com.kindeev.swipelauncher.domain.getUserImageIds

class CheckCircleMenuUseCase(
    private val context: Context,
    ) {
    fun invoke(
        circleMenu: CircleMenu
    ): Boolean {
        return circleMenu.check(
            allCircleMenuIds = LauncherData.allCircleMenus.value?.map { it.id } ?: emptyList(),
            allPackageNames = (LauncherData.allApplicationInfo.value ?: context.getApplications()).map { it.packageName },
            userImageIds = context.getUserImageIds()
        ) == null
    }
}