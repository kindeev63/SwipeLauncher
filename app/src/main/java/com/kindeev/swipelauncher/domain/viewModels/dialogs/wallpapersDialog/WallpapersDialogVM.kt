package com.kindeev.swipelauncher.domain.viewModels.dialogs.wallpapersDialog

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.entities.WallpaperData
import com.kindeev.swipelauncher.domain.useCases.wallpapers.WallpapersUseCase
import java.io.File

class WallpapersDialogVM(context: Context, dir: File) : ViewModel() {
    private val wallpapersUseCase = WallpapersUseCase(context, dir)

    private val _wallpapers = MutableLiveData(wallpapersUseCase.getWallpapers())
    val wallpapers: LiveData<List<WallpaperData>> = _wallpapers

    private val _selectedWallpapersId = MutableLiveData<List<Int>>(emptyList())
    val selectedWallpapersId: LiveData<List<Int>> = _selectedWallpapersId

    private val _deleteWallpapersDialog = MutableLiveData(false)
    val deleteWallpapersDialog: LiveData<Boolean> = _deleteWallpapersDialog

    fun addWallpaper(uri: Uri) {
        val result = wallpapersUseCase.addWallpaper(uri)
        if (result) {
            updateWallpapers()
        }
    }

    fun deleteSelectedWallpapers() {
        selectedWallpapersId.value?.let {
            wallpapersUseCase.deleteWallpapers(it)
            clearSelectedWallpapers()
            updateWallpapers()
            hideDeleteWallpapersDialog()
        }
    }

    private fun updateWallpapers() {
        _wallpapers.postValue(wallpapersUseCase.getWallpapers())
    }

    fun clearSelectedWallpapers() {
        _selectedWallpapersId.postValue(emptyList())
    }

    fun showDeleteWallpapersDialog() {
        _deleteWallpapersDialog.postValue(true)
    }

    fun hideDeleteWallpapersDialog() {
        _deleteWallpapersDialog.postValue(false)
    }

    fun selectAllWallpapers() {
        wallpapers.value?.let {
            _selectedWallpapersId.postValue(it.map { it.id })
        }
    }

    fun clickOnWallpaper(id: Int) {
        _selectedWallpapersId.value?.let {
            if (it.isNotEmpty() == true) {
                _selectedWallpapersId.postValue(
                    it.toMutableList().apply {
                        if (id in it) {
                            remove(id)
                        } else {
                            add(id)
                        }
                    }
                )
            }
        }
    }

    fun longClickOnWallpaper(id: Int) {
        _selectedWallpapersId.value?.let {
            _selectedWallpapersId.postValue(
                it.toMutableList().apply {
                    if (id in it) {
                        remove(id)
                    } else {
                        add(id)
                    }
                }
            )
        }
    }
}