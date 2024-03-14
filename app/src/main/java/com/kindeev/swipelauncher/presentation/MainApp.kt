package com.kindeev.swipelauncher.presentation

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.kindeev.swipelauncher.presentation.activities.ErrorActivity
import com.kindeev.swipelauncher.data.GlobalExceptionHandler
import com.kindeev.swipelauncher.domain.viewModels.MainAppVM

class MainApp: Application() {
    val mainAppVM by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(MainAppVM::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHandler.initialize(this, ErrorActivity::class.java)
        mainAppVM
    }
}