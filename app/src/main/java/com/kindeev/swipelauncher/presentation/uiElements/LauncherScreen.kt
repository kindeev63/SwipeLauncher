package com.kindeev.swipelauncher.presentation.uiElements

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.presentation.MainAppViewModel

@Composable
fun LauncherScreen(mainAppViewModel: MainAppViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PhoneWallpaper()
        SwipeBoxUI(
            mainAppViewModel = mainAppViewModel
        )
    }
}

@Composable
private fun PhoneWallpaper() {
    val context = LocalContext.current
    val wallpaperManager = WallpaperManager.getInstance(context)
    var wallpaperDrawable: Drawable? = null
    val permission =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_EXTERNAL_STORAGE else Manifest.permission.READ_MEDIA_IMAGES
    if (ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED) {
        wallpaperDrawable = wallpaperManager.drawable
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(permission),
            0
        )
    }
    wallpaperDrawable?.let {
        Image(
            modifier = Modifier.fillMaxSize(),
            bitmap = it.toBitmap().asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}