package com.kindeev.swipelauncher.domain.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.getOnlyChanged
import kotlinx.coroutines.launch

class AllCircleMenusScreenVM : ViewModel() {

    fun deleteCircleMenu(circleMenu: CircleMenu, context: Context) {
        viewModelScope.launch {
            if (circleMenu.id == 0) return@launch
            LauncherData.deleteCircleMenu(circleMenu)
            LauncherData.allCircleMenus.value?.getOnlyChanged(context)?.let { LauncherData.insertCircleMenus(it) }
        }
    }
}