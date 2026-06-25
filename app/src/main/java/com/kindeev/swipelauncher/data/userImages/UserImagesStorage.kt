package com.kindeev.swipelauncher.data.userImages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.text.isDigitsOnly
import com.kindeev.swipelauncher.domain.utils.userImagesDir
import java.io.File
import java.io.FileOutputStream

class UserImagesStorage(private val context: Context) {
    private val dir: File = context.userImagesDir()
    private val maxImageSize = 512

    init {
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    fun insert(uri: Uri, id: Int): Boolean =
        try {
            BitmapFactory.Options()
                .apply {
                    inJustDecodeBounds = true
                    decodeBitmap(uri)
                    inSampleSize = calculateInSampleSize(this)
                    inJustDecodeBounds = false
                }
                .decodeBitmap(uri)
                ?.use { bitmap ->
                    FileOutputStream(getFile(id)).use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                } ?: false
        } catch (_: Exception) {
            false
        }

    fun getIds(): List<Int> =
        dir.listFiles {
            it.nameWithoutExtension.isDigitsOnly() && it.extension == "png"
        }?.mapNotNull {
            it.nameWithoutExtension.toIntOrNull()
        } ?: emptyList()

    fun delete(id: Int): Boolean =
        getFile(id).let { file ->
            !file.exists() || file.delete()
        }

    fun getFile(id: Int): File = File(dir, "$id.png")


    private fun BitmapFactory.Options.decodeBitmap(uri: Uri): Bitmap? =
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, this)
        }

    private inline fun <R> Bitmap.use(block: (Bitmap) -> R): R {
        try {
            return block(this)
        } finally {
            if (!isRecycled) recycle()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options): Int =
        (minOf(options.outHeight, options.outWidth) / 2).let { dimension ->
            generateSequence(1) { it * 2 }
                .first {
                    dimension / it < maxImageSize
                }
        }
}