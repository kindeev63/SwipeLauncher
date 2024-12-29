package com.kindeev.swipelauncher.domain.viewModels.screens.wallpaperScreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.entities.WallpaperData
import com.kindeev.swipelauncher.domain.useCases.wallpapers.WallpaperAlarmUseCase
import com.kindeev.swipelauncher.domain.useCases.wallpapers.WallpapersUseCase
import com.kindeev.swipelauncher.domain.utils.wallpapersHomeScreenDir
import com.kindeev.swipelauncher.domain.utils.wallpapersLockScreenDir

class WallpaperScreenVM(context: Context): ViewModel() {
    private val homeWallpapersUseCase = WallpapersUseCase(context, context.wallpapersHomeScreenDir())
    private val lockWallpapersUseCase = WallpapersUseCase(context, context.wallpapersLockScreenDir())
    private val wallpaperAlarmUseCase = WallpaperAlarmUseCase(context)

    fun setChangeHomeScreenWallpaperAlarm(minutes: Int) {
        wallpaperAlarmUseCase.setChangeHomeScreenWallpaperAlarm(minutes)
    }

    fun setChangeLockScreenWallpaperAlarm(minutes: Int) {
        wallpaperAlarmUseCase.setChangeLockScreenWallpaperAlarm(minutes)
    }

    fun cancelChangeHomeScreenWallpaperAlarm() {
        wallpaperAlarmUseCase.cancelChangeHomeScreenWallpaperAlarm()
    }

    fun cancelChangeLockScreenWallpaperAlarm() {
        wallpaperAlarmUseCase.cancelChangeLockScreenWallpaperAlarm()
    }

    fun getHomeScreenWallpapersCount(): Int {
        return homeWallpapersUseCase.wallpapersCount()
    }

    fun getLockScreenWallpapersCount(): Int {
        return lockWallpapersUseCase.wallpapersCount()
    }

    // For WallpaperDialog

    private var wallpapersUseCase = WallpapersUseCase(context, context.wallpapersLockScreenDir())

    fun setWallpapersHomeScreenDir() {
        wallpapersUseCase = homeWallpapersUseCase
    }

    fun setWallpapersLockScreenDir() {
        wallpapersUseCase = lockWallpapersUseCase
    }

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