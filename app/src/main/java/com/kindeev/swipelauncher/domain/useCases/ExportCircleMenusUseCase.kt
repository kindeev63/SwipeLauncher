package com.kindeev.swipelauncher.domain.useCases

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.dataBase.typeConverter.DataBaseTypeConverter
import com.kindeev.swipelauncher.domain.utils.userImagesDir
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExportCircleMenusUseCase(private val context: Context) {
    fun export(circleMenus: List<CircleMenu>): Boolean {
        return try {
            val jsonFile = saveCircleMenusToJsonFile(circleMenus)
            val images =
                circleMenus.getUserImageNamesFromCircleMenus().map { File(context.userImagesDir(), it) }
            val files = images.toMutableList().apply { add(jsonFile) }.toList()
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFileWithMediaStore(files)
            } else {
                saveBackup(files)
            }
            jsonFile.delete()
            return result
        } catch (_: Exception) {
            false
        }
    }

    private fun saveCircleMenusToJsonFile(circleMenus: List<CircleMenu>): File {
        val typeConverter = DataBaseTypeConverter()
        val gson = Gson()
        val content = gson.toJson(circleMenus.map {
            gson.toJson(
                CircleMenuToSave(
                    id = it.id,
                    title = it.title,
                    items = typeConverter.fromCircleMenuItems(it.items)
                )
            )
        })
        val tempFile = File(context.cacheDir, "data.json")
        tempFile.writeText(content)
        return tempFile
    }

    private fun List<CircleMenu>.getUserImageNamesFromCircleMenus(): List<String> {
        return this
            .asSequence()
            .map { it.items } // get lists of items
            .flatten() // get one list with all items
            .map { it.image } // list with CircleMenuImage
            .filter { it is UserImage } // list with UserImages
            .map { "${(it as UserImage).id}.png" }
            .toList() // list of filenames
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileWithMediaStore(files: List<File>): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "backup.zip")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { fileUri ->
            try {
                context.contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                    ZipOutputStream(outputStream).use { zos ->
                        files.forEach { file ->
                            val entryName = file.name
                            FileInputStream(file).use { fis ->
                                zos.putNextEntry(ZipEntry(entryName))
                                fis.copyTo(zos)
                            }
                        }
                    }
                    return true
                }
            } catch (_: Exception) {
                return false
            }
        }
        return false
    }

    private fun saveBackup(files: List<File>): Boolean {
        try {
            FileOutputStream(File(getDownloadsDir(), "backup.zip")).use { out ->
                ZipOutputStream(out).use { zos ->
                    files.forEach { file ->
                        val entryName = file.name
                        FileInputStream(file).use { fis ->
                            zos.putNextEntry(ZipEntry(entryName))
                            fis.copyTo(zos)
                        }
                    }
                }
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

    private fun getDownloadsDir(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    private data class CircleMenuToSave(val id: Int, val title: String, val items: String)
}