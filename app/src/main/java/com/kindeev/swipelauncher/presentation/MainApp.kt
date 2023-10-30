package com.kindeev.swipelauncher.presentation

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.kindeev.swipelauncher.domain.MainAppViewModel

class MainApp: Application() {
    val mainAppViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            .create(MainAppViewModel::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        mainAppViewModel
    }
}