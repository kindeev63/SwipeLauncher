package com.kindeev.swipelauncher.domain

class CircleMenuImage {
    var type: String = NONE_IMAGE
    var data: Any? = null
    companion object {
        const val NONE_IMAGE = "none_image"
        const val APP_IMAGE = "app_image"
        const val DEFAULT_IMAGE = "default_image"
    }
}