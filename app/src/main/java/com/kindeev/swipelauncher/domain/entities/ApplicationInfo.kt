package com.kindeev.swipelauncher.domain.entities

import android.content.ComponentName
import java.io.Serializable

data class ApplicationInfo(
    val title: String,
    val componentName: ComponentName,
    val packageName: String
): Serializable
