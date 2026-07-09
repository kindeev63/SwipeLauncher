package com.kindeev.swipelauncher.data.backup.entities

import java.io.File

data class ZipFileData(
    val circleMenusFile: File,
    val userImageFiles: List<File>
)