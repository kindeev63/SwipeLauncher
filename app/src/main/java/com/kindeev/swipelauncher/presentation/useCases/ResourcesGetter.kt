package com.kindeev.swipelauncher.presentation.useCases

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.kindeev.swipelauncher.presentation.interfaces.DrawableGetter
import com.kindeev.swipelauncher.presentation.interfaces.StringGetter

class ResourcesGetter(
    private val context: Context
): StringGetter, DrawableGetter {
    override fun getString(resId: Int) = context.getString(resId)

    override fun getDrawable(resId: Int) = ResourcesCompat.getDrawable(context.resources, resId, context.theme)
}