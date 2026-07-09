package com.kindeev.swipelauncher.data.coil

import android.content.Context
import android.content.pm.LauncherApps
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

fun initCoil(context: Context)  {
    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    Coil.setImageLoader(
        ImageLoader.Builder(context)
            .components {
                add(AppIconFetcher.Factory(context, launcherApps))
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.4)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .allowHardware(true)
            .build()
    )
}
