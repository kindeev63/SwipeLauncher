package com.kindeev.swipelauncher.presentation

import android.app.Application
import com.kindeev.swipelauncher.domain.utils.getMinScreenLength
import com.kindeev.swipelauncher.domain.utils.setActionAndImageTypes
import com.kindeev.swipelauncher.presentation.activities.ErrorActivity
import com.kindeev.swipelauncher.presentation.useCases.AppInitializer
import com.kindeev.swipelauncher.domain.Constants

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHandler.initialize(this, ErrorActivity::class.java)
        DI.init(this)
        initApp()
    }

    private fun initApp() {
        Constants.minScreenLength = getMinScreenLength()
        setActionAndImageTypes()
        DI.getFactory<AppInitializer>().initialize()
    }
}
