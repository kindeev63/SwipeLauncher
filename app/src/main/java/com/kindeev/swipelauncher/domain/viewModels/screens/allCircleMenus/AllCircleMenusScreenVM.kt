package com.kindeev.swipelauncher.domain.viewModels.screens.allCircleMenus

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.circle_menu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.useCases.ExportCircleMenusUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase
import com.kindeev.swipelauncher.domain.useCases.ImportCircleMenusUseCase
import com.kindeev.swipelauncher.domain.useCases.UserImagesUseCase
import kotlinx.coroutines.launch

class AllCircleMenusVM(context: Context) : ViewModel() {
    private val _selectedMenuIds = MutableLiveData<List<Int>>(emptyList())
    val selectedMenuIds: LiveData<List<Int>> = _selectedMenuIds
    private val userImagesUseCase = UserImagesUseCase(context)
    private val importCircleMenusUseCase = ImportCircleMenusUseCase(context, userImagesUseCase)
    private val exportCircleMenusUseCase = ExportCircleMenusUseCase(context)
    private val getItemImageUseCase = GetItemImageUseCase(context)

    fun selectAllMenus(allMenus: List<CircleMenu>) {
        _selectedMenuIds.postValue(allMenus.map { it.id })
    }

    fun deleteSelectedMenus(allMenus: List<CircleMenu>) = viewModelScope.launch {
        LauncherData.deleteCircleMenus(allMenus.filter { selectedMenuIds.value?.contains(it.id) == true }
            .filter { it.id != 0 })
        _selectedMenuIds.postValue(emptyList())
    }

    fun exportSelectedMenus(allMenus: List<CircleMenu>, onFinish: (Boolean) -> Unit) {
        val result = exportCircleMenusUseCase.export(allMenus.filter { selectedMenuIds.value?.contains(it.id) == true })
        _selectedMenuIds.postValue(emptyList())
        onFinish(result)
    }

    fun importCircleMenus(uri: Uri?, onFinish: (Boolean) -> Unit) = viewModelScope.launch {
        val result = importCircleMenusUseCase.import(uri ?: return@launch)
        onFinish(result)
    }

    fun finishSelect() {
        _selectedMenuIds.postValue(emptyList())
    }

    fun changeSelectionStateOf(circleMenu: CircleMenu) {
        _selectedMenuIds.postValue(
            selectedMenuIds.value?.toMutableList()?.apply {
                if (contains(circleMenu.id)) {
                    remove(circleMenu.id)
                } else {
                    add(circleMenu.id)
                }
            }
        )
    }

    fun getItemImage(circleMenuImage: CircleMenuImage): ImageBitmap? {
        return getItemImageUseCase.getItemImage(circleMenuImage)
    }
}