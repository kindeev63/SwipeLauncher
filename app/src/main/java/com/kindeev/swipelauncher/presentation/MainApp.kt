package com.kindeev.swipelauncher.presentation

import android.app.Application
import com.kindeev.swipelauncher.presentation.activities.ErrorActivity
import com.kindeev.swipelauncher.presentation.useCases.AppInitializer

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHandler.initialize(this, ErrorActivity::class.java)
        DI.init(this)
        initApp()
    }

    private fun initApp() {
        DI.getFactory<AppInitializer>().initialize()
    }
}
