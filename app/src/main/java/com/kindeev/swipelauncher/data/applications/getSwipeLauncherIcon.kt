package com.kindeev.swipelauncher.data.applications

import android.content.Context

fun Context.getSwipeLauncherIcon() = applicationInfo.loadIcon(packageManager)