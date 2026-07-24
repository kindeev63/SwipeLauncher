package com.kindeev.swipelauncher.domain.useCases.circleMenuActions

import android.content.Context
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FlashLightUseCase(private val context: Context) {

    private var _flashLightState = FlashLightState.Off
    val flashLightState: FlashLightState
        get() = _flashLightState

    suspend fun on() = withContext(Dispatchers.Default) {
        val cameraManager =
            context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
        _flashLightState = FlashLightState.On
    }

    suspend fun off() = withContext(Dispatchers.Default) {
        val cameraManager =
            context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
        _flashLightState = FlashLightState.Off
    }

    enum class FlashLightState {
        On, Off
    }
}