package com.kindeev.swipelauncher.data.backup

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.kindeev.swipelauncher.data.backup.entities.ZipFileData
import com.kindeev.swipelauncher.data.entities.CircleMenuEntity
import com.kindeev.swipelauncher.data.entities.mappers.toEntity
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.collections.forEach

class ExportCircleMenusUseCase(
    private val userImagesRepository: UserImagesRepository,
    context: Context,
) {
    private val appContext = context.applicationContext
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }


    fun export(circleMenus: List<CircleMenu>): Boolean =
        circleMenus.toFiles().save()

    private fun List<CircleMenuEntity>.toJson(): String =
        json.encodeToString(ListSerializer(CircleMenuEntity.serializer()), this)

    private fun List<CircleMenu>.toEntity(): List<CircleMenuEntity> =
        map { it.toEntity() }

    private fun List<CircleMenu>.toFile(): File =
        File(appContext.cacheDir, "data.json").apply {
            writeText(this@toFile.toEntity().toJson())
        }

    private fun List<CircleMenu>.getUserImageIds(): List<Int> =
        flatMap { it.items }
            .map { it.image }
            .filterIsInstance<UserImage>()
            .map { it.id }

    private fun List<CircleMenu>.toFiles(): ZipFileData =
        ZipFileData(
            circleMenusFile = toFile(),
            userImageFiles = getUserImageIds().map { userImagesRepository.getFile(it) }
        )

    private fun ZipFileData.save(): Boolean =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveWithMediaStore(toFiles())
            } else {
                saveToDownloads(toFiles())
                true
            }
        } catch (_: Exception) {
            false
        } finally {
            if (circleMenusFile.exists()) circleMenusFile.delete()
        }

    private val downloadsDir: File
        get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveWithMediaStore(files: List<File>): Boolean =
        appContext.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "backup.zip")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        ).let { uri ->
            if (uri == null) {
                false
            } else {
                appContext.contentResolver.openOutputStream(uri).use { outputStream ->
                    ZipOutputStream(outputStream).saveFiles(files)
                }
                true
            }
        }

    private fun saveToDownloads(files: List<File>) =
        FileOutputStream(File(downloadsDir, "backup.zip")).use { out ->
            ZipOutputStream(out).saveFiles(files)
        }

    private fun ZipOutputStream.saveFiles(files: List<File>) =
        use { zos ->
            files.forEach { file ->
                FileInputStream(file).use { fis ->
                    zos.putNextEntry(ZipEntry(file.name))
                    fis.copyTo(zos)
                }
            }
        }

    private fun ZipFileData.toFiles(): List<File> = userImageFiles + circleMenusFile
}