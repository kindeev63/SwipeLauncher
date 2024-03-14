package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import android.hardware.camera2.CameraManager
import kotlin.concurrent.thread

class FlashLightUseCase(private val context: Context) {
    fun on() {
        thread {
            val cameraManager =
                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
        }
    }

    fun off() {
        thread {
            val cameraManager =
                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
        }
    }
}