package com.kindeev.swipelauncher.domain.entities

import android.content.ComponentName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApplicationInfo(
    val packageName: String,
    val title: String,
    val componentName: ComponentName
): Parcelable
