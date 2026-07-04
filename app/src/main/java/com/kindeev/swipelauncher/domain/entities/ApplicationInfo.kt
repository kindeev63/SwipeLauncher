package com.kindeev.swipelauncher.domain.entities

import android.content.ComponentName
import java.io.Serializable

data class ApplicationInfo(
    val packageName: String,
    val title: String,
    val componentName: ComponentName
): Serializable
