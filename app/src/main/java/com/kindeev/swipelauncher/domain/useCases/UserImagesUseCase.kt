package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.data.dataBases.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.circle_menu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.utils.userImagesDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UserImagesUseCase(private val context: Context) {

    fun getUserImageIds(): List<Int> {
        val ids = mutableListOf<Int>()
        context.userImagesDir().listFiles()?.forEach { file ->
            file.name.split(".")[0].toIntOrNull()?.let {
                ids.add(it)
            }
        }
        return ids
    }

    fun getUserImages(): Map<Int, ImageBitmap> {
        val newUserImages = mutableMapOf<Int, ImageBitmap>()
        context.userImagesDir().listFiles()?.map { file ->
            file.name.split(".")[0].toIntOrNull()?.let { key ->
                newUserImages[key] = BitmapFactory.decodeFile(file.path).asImageBitmap()
            }
        } ?: return emptyMap()
        return newUserImages
    }

    fun removeUnusedUserImages(
        allCircleMenus: List<CircleMenu>,
        allApplicationData: List<ApplicationData>
    ) {
        val allUserImageNamesInCircleMenus = allCircleMenus.getUserImageNamesFromCircleMenus()
        val allUserImageNamesInApplicationData =
            allApplicationData.getUserImageNamesFromApplicationsData()
        context.userImagesDir().listFiles()?.forEach { file ->
            if (file.name.contains(".png") && file.name !in allUserImageNamesInCircleMenus && file.name !in allUserImageNamesInApplicationData) {
                file.delete()
            }
        }
    }

    fun addUserImage(uri: Uri): UserImage? {
        val ids = LauncherData.userImages.map { it.key }
        var newId = 0
        while (newId in ids) {
            newId++
        }
        val bitmap = createBitmap(uri) ?: return null
        LauncherData.userImages = LauncherData.userImages.toMutableMap().apply {
            this[newId] = bitmap.asImageBitmap()
        }.toMap()
        saveUserImage(newId, bitmap)
        return UserImage(newId)
    }

    private fun saveUserImage(id: Int, bitmap: Bitmap) {
        val file = File(context.userImagesDir(), "$id.png")
        file.createNewFile()
        val fos = FileOutputStream(file)
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        fos.write(bos.toByteArray())
        fos.flush()
        fos.close()
    }

    private fun createBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images
                    .Media.getBitmap(context.contentResolver, uri)

            } else {
                ImageDecoder.decodeBitmap(
                    ImageDecoder
                        .createSource(context.contentResolver, uri)
                )
            }
        } catch (_: Exception) {
            null
        }
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

    private fun List<ApplicationData>.getUserImageNamesFromApplicationsData(): List<String> {
        return this
            .asSequence()
            .map { it.image } // get lists of items
            .filter { it is UserImage } // list with UserImages
            .map { "${(it as UserImage).id}.png" }
            .toList() // list of filenames
    }
}