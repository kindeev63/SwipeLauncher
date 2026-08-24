package com.kindeev.swipelauncher.presentation.interfaces

import android.graphics.drawable.Drawable

interface DrawableGetter {
    fun getDrawable(resId: Int): Drawable?
}