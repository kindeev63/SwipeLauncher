package com.kindeev.swipelauncher.domain.utils

import android.content.Context
import com.kindeev.swipelauncher.domain.Constants
import java.io.File

fun Context.userImagesDir(): File {
    val file = File(filesDir, Constants.USER_IMAGES_DIR)
    if (!file.exists()) {
        file.mkdirs()
    }
    return file
}


fun Context.checkDirs() {
    filesDir.listFiles()?.forEach { file ->
        if (file.isFile) {
            if (file.name.contains(".png")) {
                file.renameTo(File(userImagesDir(), file.name))
            } else {
                file.delete()
            }
        }
    }
}