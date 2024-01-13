package com.kindeev.swipelauncher.presentation.viewModels

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.ApplicationData
import com.kindeev.swipelauncher.data.dataBaseElements.AppDataBase
import com.kindeev.swipelauncher.domain.AppDao
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuUseCases.DeleteCircleMenuUseCase
import com.kindeev.swipelauncher.domain.circleMenuUseCases.GetAllCircleMenuUseCase
import com.kindeev.swipelauncher.domain.circleMenuUseCases.GetCircleMenuUseCase
import com.kindeev.swipelauncher.domain.circleMenuUseCases.InsertCircleMenuUseCase
import kotlinx.coroutines.launch

class MainAppViewModel(application: Application): AndroidViewModel(application) {
    private val appDao: AppDao
    private val getCircleMenuUseCase: GetCircleMenuUseCase
    private val insertCircleMenuUseCase: InsertCircleMenuUseCase
    private val deleteCircleMenuUseCase: DeleteCircleMenuUseCase
    private val getAllCircleMenuUseCase: GetAllCircleMenuUseCase
    val allCircleMenu: LiveData<List<CircleMenu>>
    var allApplicationData = emptyList<ApplicationData>()

    init {
        appDao = AppDataBase.getDataBase(application).getDao()
        getCircleMenuUseCase = GetCircleMenuUseCase(appDao)
        insertCircleMenuUseCase = InsertCircleMenuUseCase(appDao)
        deleteCircleMenuUseCase = DeleteCircleMenuUseCase(appDao)
        getAllCircleMenuUseCase = GetAllCircleMenuUseCase(appDao)
        allCircleMenu = getAllCircleMenuUseCase.get()
        allApplicationData =
            application.applicationContext.packageManager.getInstalledApplications(PackageManager.MATCH_ALL).filter {
                (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0
            }.map {
                ApplicationData(
                    name = it.loadLabel(application.applicationContext.packageManager).toString(),
                    icon = it.loadIcon(application.applicationContext.packageManager).toBitmap().asImageBitmap(),
                    packageName = it.packageName
                )
            }
    }
    fun insertCircleMenu(circleMenu: CircleMenu) = viewModelScope.launch {
        insertCircleMenuUseCase.insertCircleMenu(circleMenu)
    }

    fun insertCircleMenus(circleMenus: List<CircleMenu>) = viewModelScope.launch {
        insertCircleMenuUseCase.insertCircleMenus(circleMenus)
    }

    fun deleteCircleMenu(circleMenu: CircleMenu) = viewModelScope.launch {
        deleteCircleMenuUseCase.delete(circleMenu)
    }

    fun getCircleMenuById(id: Int, function: (CircleMenu?) -> Unit) {
        viewModelScope.launch {
            function(getCircleMenuUseCase.getById(id))
        }
    }
}