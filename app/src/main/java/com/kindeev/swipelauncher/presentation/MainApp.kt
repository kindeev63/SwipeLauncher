package com.kindeev.swipelauncher.presentation

import android.app.Application
import com.kindeev.swipelauncher.di.AppContainer
import com.kindeev.swipelauncher.presentation.activities.ErrorActivity

class MainApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHandler.initialize(this, ErrorActivity::class.java)

        container = AppContainer(this)
    }

}