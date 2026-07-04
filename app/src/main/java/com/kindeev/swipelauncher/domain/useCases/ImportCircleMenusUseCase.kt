package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kindeev.swipelauncher.di.container
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.dataBase.typeConverter.CircleMenuTypeConverter
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import com.kindeev.swipelauncher.domain.utils.userImagesDir
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import kotlin.collections.filter
import kotlin.collections.forEach

class ImportCircleMenusUseCase(
    private val context: Context,
    private val userImagesRepository: UserImagesRepository
) {
    private val gson = Gson()

    suspend fun import(
        uri: Uri,

    ): Boolean {
        val files = getFilesFromZip(uri)
        val circleMenus =
            files?.find { it.name == "data.json" }?.getCircleMenusFromJson() ?: return false
        val circleMenuIds = getNewCircleMenuIds(circleMenus)
        val userImageIds = getNewUserImages(circleMenus)
        val newCircleMenus = circleMenus.getNewCircleMenus(
            circleMenuIds = circleMenuIds,
            userImageIds = userImageIds
        )
        addUserImages(files.filter { it.name != "data.json" }, userImageIds)
        userImagesRepository.prefetchAll()
        context.container.dataRepository.insertCircleMenus(newCircleMenus)
        files.forEach { it.delete() }
        return true
    }

    private fun getFilesFromZip(uri: Uri): List<File>? {
        try {
            val files = mutableListOf<File>()
            context.contentResolver.openInputStream(uri).use { input ->
                ZipInputStream(input).use { zis ->
                    var zipEntry = zis.nextEntry
                    while (zipEntry != null) {
                        val file = File(context.cacheDir, zipEntry.name)
                        FileOutputStream(file).use { fos ->
                            val buffer = ByteArray(1024)
                            var length: Int
                            while (zis.read(buffer).also { length = it } > 0) {
                                fos.write(buffer, 0, length)
                            }
                        }
                        files.add(file)
                        zipEntry = zis.nextEntry
                    }
                }
            }
            return files
        } catch (_: Exception) {
            return null
        }
    }

    private fun File.getCircleMenusFromJson(): List<CircleMenu>? {
        try {
            val gson = Gson()
            val json = readText()
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson<List<String>>(json, type)
                .map { gson.fromJson(it, CircleMenuToSave::class.java) }
                .map {
                    CircleMenu(
                        id = it.id,
                        title = it.title,
                        items = it.items.toCircleMenuItems()
                    )
                }
        } catch (_: Exception) {
            return null
        }
    }

    private fun String.toCircleMenuItems(): List<CircleMenuItem> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson<List<String>>(this, type).map { it.toCircleMenuItem() }.sortedBy { it.index }.map { it.item }
    }

    private fun String.toCircleMenuItem(): CircleMenuItemWithIndex {
        val typeConverter = CircleMenuTypeConverter()
        val circleMenuItemToSave = gson.fromJson(this, CircleMenuItemToSave::class.java)
        val offset = gson.fromJson(circleMenuItemToSave.offset, Offset::class.java)
        val image = typeConverter.toCircleMenuImage(circleMenuItemToSave.image)
        val action = typeConverter.toCircleMenuAction(circleMenuItemToSave.action)
        return CircleMenuItemWithIndex(
            index = menuCords.indexOf(offset),
            item = CircleMenuItem(
                image = image,
                action = action
            )
        )
    }

    private class CircleMenuItemToSave(
        val offset: String,
        val image: String,
        val action: String
    )

    private val menuCords = listOf(
        Offset(0f, -4f), // 1
        Offset(3f, -3f), // 2
        Offset(4f, 0f), // 3
        Offset(3f, 3f), // 4
        Offset(0f, 4f), // 5
        Offset(-3f, 3f), // 6
        Offset(-4f, 0f), // 7
        Offset(-3f, -3f), // 8
    )

    private class CircleMenuItemWithIndex(val index: Int, val item: CircleMenuItem)

    private suspend fun getNewUserImages(
        circleMenus: List<CircleMenu>
    ): Map<Int, Int> {
        val userImageIds = mutableMapOf<Int, Int>()
        val existingUserImagesIds = userImagesRepository.getAllIds()
        circleMenus.getUserImageIdsFromCircleMenus().forEach { id ->
            var newId = id
            while (newId in existingUserImagesIds || newId in userImageIds.values) {
                newId++
            }
            userImageIds[id] = newId
        }
        return userImageIds
    }

    private fun List<CircleMenu>.getUserImageIdsFromCircleMenus(): List<Int> {
        return this
            .asSequence().flatMap { it.items } // get one list with all items
            .map { it.image } // list with CircleMenuImage
            .filter { it is UserImage } // list with UserImages
            .map { (it as UserImage).id }
            .toList() // list of ids
    }

    private fun getNewCircleMenuIds(circleMenus: List<CircleMenu>): Map<Int, Int> {
        val circleMenuIds = mutableMapOf<Int, Int>()
        val existingCircleMenuIds =
            context.container.circleMenus.value.map { it.id }.filter { it != 0 }
        circleMenus.map { it.id }.forEach { id ->
            var newId = id
            while (newId in existingCircleMenuIds || newId in circleMenuIds.values) {
                newId++
            }
            circleMenuIds[id] = newId
        }
        return circleMenuIds
    }

    private fun List<CircleMenu>.getNewCircleMenus(
        circleMenuIds: Map<Int, Int>,
        userImageIds: Map<Int, Int>
    ): List<CircleMenu> {
        val newCircleMenus = mutableListOf<CircleMenu>()
        forEach { circleMenu ->
            newCircleMenus.add(
                CircleMenu(
                    id = circleMenuIds[circleMenu.id] ?: 0,
                    title = circleMenu.title,
                    items = circleMenu.items.getNewItems(
                        circleMenuIds = circleMenuIds,
                        userImageIds = userImageIds
                    )
                )
            )
        }
        return newCircleMenus
    }

    private fun List<CircleMenuItem>.getNewItems(
        circleMenuIds: Map<Int, Int>,
        userImageIds: Map<Int, Int>
    ): List<CircleMenuItem> {
        val newItems = mutableListOf<CircleMenuItem>()
        forEach { item ->
            newItems.add(
                CircleMenuItem(
                    action = when (item.action) {
                        is OpenCircleMenuAction -> {
                            OpenCircleMenuAction(id = circleMenuIds[item.action.id] ?: 0)
                        }

                        else -> item.action
                    },
                    image = when (item.image) {
                        is UserImage -> {
                            UserImage(id = userImageIds[item.image.id] ?: 0)
                        }

                        else -> item.image
                    }
                )
            )
        }
        return newItems
    }

    private fun addUserImages(files: List<File>, userImageIds: Map<Int, Int>) {
        files.forEach { file ->
            userImageIds[file.name.split(".")[0].toIntOrNull()]?.let { id ->
                file.copyTo(
                    File(context.userImagesDir(), "$id.png")
                )
            }
        }
    }

    private data class CircleMenuToSave(val id: Int, val title: String, val items: String)
}