package com.kindeev.swipelauncher.data.backup

import android.content.Context
import android.net.Uri
import com.kindeev.swipelauncher.data.entities.CircleMenuEntity
import com.kindeev.swipelauncher.data.entities.mappers.fromEntity
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ImportCircleMenusUseCase(
    private val userImagesRepository: UserImagesRepository,
    private val dataRepository: DataRepository,
    context: Context
) {
    private val appContext = context.applicationContext

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun import(uri: Uri): Boolean =
        try {
            val (data, userImageIdsMapper) = uri.getFiles()
            if (data == null) {
                false
            } else {
                val circleMenuIds = dataRepository.getCircleMenuIds()
                val newCircleMenus = data.getCircleMenus().changeIds(
                    userImageIdsMapper = userImageIdsMapper,
                    userCircleMenuIds = circleMenuIds.toSet()
                )
                dataRepository.insertCircleMenus(newCircleMenus)
                true
            }
        } catch (_: Exception) {
            false
        }

    /**
     * Получает все файлы из zip по uri
     * Возвращает File - data.json и mapper для id новых userImages
     * (старый id - ключ, новый - значение)
     * Также сохраняет userImages в память
     */
    private suspend fun Uri.getFiles(): Pair<File?, Map<Int, Int>> =
        appContext.contentResolver.openInputStream(this).use { input ->
            ZipInputStream(input).use { zis ->
                val usedIds = userImagesRepository.getAllIds().toMutableSet()
                val userImageIdsMapper = mutableMapOf<Int, Int>()
                var data: File? = null
                zis.entryIterator().forEach { entry ->
                    if (entry.name == "data.json") {
                        data = File(appContext.cacheDir, "data.json").apply {
                            writeFromZip(zis)
                        }
                    } else {
                        val oldId = entry.name.split(".").first().toInt()
                        val id = if (oldId in usedIds) {
                            val newId = usedIds.findFreeId()
                            usedIds.add(newId)
                            userImageIdsMapper[oldId] = newId
                            newId
                        } else oldId
                        userImagesRepository.getFile(id).writeFromZip(zis)
                    }
                }
                data to userImageIdsMapper
            }
        }

    private fun File.getCircleMenus(): List<CircleMenuEntity> =
        json.decodeFromString(ListSerializer(CircleMenuEntity.serializer()), readText())

    private fun List<CircleMenuEntity>.changeIds(
        userImageIdsMapper: Map<Int, Int>,
        userCircleMenuIds: Set<Int>
    ): List<CircleMenu> {
        val usedIds = userCircleMenuIds.toMutableSet()
        val circleMenuIdsMapper = mutableMapOf<Int, Int>()
        map { it.id }.forEach { id ->
            if (id != 0 && id in usedIds) {
                val newId = usedIds.findFreeId()
                usedIds.add(newId)
                circleMenuIdsMapper[id] = newId
            }
        }
        return map {
            it.fromEntity().let { menu ->
                menu.copy(
                    id = circleMenuIdsMapper.getOrDefault(menu.id, it.id),
                    items = menu.items.map { item ->
                        if (item.image is UserImage && item.image.id in userImageIdsMapper) {
                            item.copy(
                                image = UserImage(
                                    userImageIdsMapper.getOrDefault(
                                        item.image.id,
                                        item.image.id
                                    )
                                )
                            )
                        } else item
                    }
                )
            }
        }
    }

    private fun Set<Int>.findFreeId(): Int {
        return generateSequence(0) { it + 1 }
            .first { it !in this }
    }

    fun ZipInputStream.entryIterator(): Iterator<ZipEntry> =
        iterator {
            var zipEntry = nextEntry
            while (zipEntry != null) {
                yield(zipEntry)
                zipEntry = nextEntry
            }
        }

    fun File.writeFromZip(zipInputStream: ZipInputStream) =
        FileOutputStream(this).use { fos ->
            val buffer = ByteArray(1024)
            var length: Int
            while (zipInputStream.read(buffer).also { length = it } > 0) {
                fos.write(buffer, 0, length)
            }
        }
}