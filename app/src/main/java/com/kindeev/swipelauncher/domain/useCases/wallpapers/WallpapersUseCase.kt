package com.kindeev.swipelauncher.domain.useCases.wallpapers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asImageBitmap
import com.kindeev.swipelauncher.domain.entities.WallpaperData
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class WallpapersUseCase(private val context: Context, private val dir: File) {

    fun getWallpapers(): List<WallpaperData> {
        val wallpapers = mutableListOf<WallpaperData>()
        dir.listFiles()?.forEach { file ->
            file.name.split(".")[0].toIntOrNull()?.let { id ->
                wallpapers.add(
                    WallpaperData(
                        id,
                        BitmapFactory.decodeFile(file.path).asImageBitmap()
                    )
                )
            }
        }
        return wallpapers
    }

    fun getWallpaper(id: Int): Bitmap? {
        return BitmapFactory.decodeFile(
            dir.listFiles()?.find { it.name.split(".")[0].toIntOrNull() == id }?.path ?: return null
        )
    }

    fun wallpapersCount(): Int {
        return dir.listFiles()?.count() ?: 0
    }

    fun addWallpaper(
        uri: Uri
    ): Boolean {
        try {
            val ids = getWallpapers().map { it.id }
            var newId = 0
            while (newId in ids) {
                newId++
            }
            val bitmap = createBitmap(uri)
            saveWallpaper(newId, dir, bitmap)
            return true
        } catch (_: Exception) {
            return false
        }
        return false
    }

    fun deleteWallpapers(
        ids: List<Int>
    ) {
        dir.listFiles()?.forEach { file ->
            if (file.name.contains(".png") && file.name.split(".")[0].toIntOrNull() in ids) {
                file.delete()
            }
        }
    }

    private fun saveWallpaper(id: Int, dir: File, bitmap: Bitmap) {
        val file = File(dir, "$id.png")
        file.createNewFile()
        val fos = FileOutputStream(file)
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        fos.write(bos.toByteArray())
        fos.flush()
        fos.close()
    }

    private fun createBitmap(uri: Uri): Bitmap = if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        MediaStore.Images
            .Media.getBitmap(context.contentResolver, uri)

    } else {
        ImageDecoder.decodeBitmap(
            ImageDecoder
                .createSource(context.contentResolver, uri)
        )
    }
}