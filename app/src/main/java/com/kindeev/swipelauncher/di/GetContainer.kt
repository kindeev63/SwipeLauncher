package com.kindeev.swipelauncher.di

import android.content.Context
import com.kindeev.swipelauncher.presentation.MainApp

val Context.container: AppContainer
    get() = (this.applicationContext as MainApp).container