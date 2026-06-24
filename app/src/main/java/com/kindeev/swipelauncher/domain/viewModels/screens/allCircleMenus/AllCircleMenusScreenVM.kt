package com.kindeev.swipelauncher.domain.viewModels.screens.allCircleMenus

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.useCases.ExportCircleMenusUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase
import com.kindeev.swipelauncher.domain.useCases.ImportCircleMenusUseCase
import com.kindeev.swipelauncher.domain.useCases.UserImagesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AllCircleMenusScreenVM(context: Context) : ViewModel() {
    private val _selectedMenuIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedMenuIds: StateFlow<List<Int>> = _selectedMenuIds
    private val userImagesUseCase = UserImagesUseCase(context)
    private val importCircleMenusUseCase = ImportCircleMenusUseCase(context, userImagesUseCase)
    private val exportCircleMenusUseCase = ExportCircleMenusUseCase(context)
    private val getItemImageUseCase = GetItemImageUseCase(context)

    fun selectAllMenus(allMenus: List<CircleMenu>) {
        _selectedMenuIds.value = allMenus.map { it.id }
    }

    fun deleteSelectedMenus(allMenus: List<CircleMenu>) = viewModelScope.launch {
        LauncherData.deleteCircleMenus(allMenus.filter { selectedMenuIds.value.contains(it.id) }
            .filter { it.id != 0 })
        _selectedMenuIds.value = emptyList()
    }

    fun exportSelectedMenus(allMenus: List<CircleMenu>, onFinish: (Boolean) -> Unit) {
        val result = exportCircleMenusUseCase.export(allMenus.filter { selectedMenuIds.value.contains(it.id) })
        _selectedMenuIds.value = emptyList()
        onFinish(result)
    }

    fun importCircleMenus(uri: Uri?, onFinish: (Boolean) -> Unit) = viewModelScope.launch {
        val result = importCircleMenusUseCase.import(uri ?: return@launch)
        onFinish(result)
    }

    fun finishSelect() {
        _selectedMenuIds.value = emptyList()
    }

    fun changeSelectionStateOf(circleMenu: CircleMenu) {
        _selectedMenuIds.value =
            selectedMenuIds.value.toMutableList().apply {
                if (contains(circleMenu.id)) {
                    remove(circleMenu.id)
                } else {
                    add(circleMenu.id)
                }
            }
    }

    fun getItemImage(circleMenuImage: CircleMenuImage): ImageBitmap? {
        return getItemImageUseCase.getItemImage(circleMenuImage)
    }
}