package com.kindeev.swipelauncher.domain.dataBase

import androidx.compose.ui.geometry.Offset
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.CallAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.DialAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOffAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOnAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenUrlAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImages
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingValue
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.ClickOnClock
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.OpenLastApp
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.PickAppActionWithImage
import com.kindeev.swipelauncher.domain.dataBase.typeConverter.DataBaseTypeConverter
import org.json.JSONObject

object Migrations {
    private val gson = Gson()

    object Migration_1_2 {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                migrateCircleMenus(database)
                migrateSettings(database)
            }
        }

        private fun migrateSettings(database: SupportSQLiteDatabase) {
            val newSettings = mutableListOf<SettingData>()
            val oldSettings = database.query("SELECT * FROM table_settings")
            oldSettings.moveToFirst()
            do {
                val name = SettingNames.valueOf(oldSettings.getString(oldSettings.getColumnIndexOrThrow("setting")))
                val settingData = SettingData(
                    name = SettingNames.valueOf(oldSettings.getString(oldSettings.getColumnIndexOrThrow("setting"))),
                    value = oldSettings.getString(oldSettings.getColumnIndexOrThrow("value")).getSettingValue(name)
                )
                newSettings.add(settingData)
            } while (oldSettings.moveToNext())
            database.execSQL("DROP TABLE IF EXISTS table_settings")
            database.execSQL("CREATE TABLE table_settings (name TEXT PRIMARY KEY NOT NULL, value TEXT NOT NULL)")
            newSettings.forEach { settingData ->
                database.execSQL("INSERT INTO table_settings (name, value) VALUES (?, ?)", arrayOf(settingData.name, DataBaseTypeConverter().fromSettingValue(settingData.value)))
            }
        }

        private fun String.getSettingValue(name: SettingNames): SettingValue {
            return when (name) {
                SettingNames.OpenLastApp -> OpenLastApp(gson.fromJson(this, Boolean::class.java))
                SettingNames.ClickOnClock -> {
                    val oldClickOnClock = JSONObject(this)
                    ClickOnClock(
                        enabled = oldClickOnClock.getBoolean("enabled"),
                        action = oldClickOnClock.getString("action").toCircleMenuAction()
                    )
                }

                SettingNames.BlackTextColorOnWallpaper -> BlackTextColorOnWallpaper(
                    gson.fromJson(
                        this,
                        Boolean::class.java
                    )
                )

                SettingNames.PickAppActionWithImage -> PickAppActionWithImage(
                    gson.fromJson(
                        this,
                        Boolean::class.java
                    )
                )
            }
        }


        private fun migrateCircleMenus(database: SupportSQLiteDatabase) {
            val newMenus = mutableListOf<CircleMenu>()
            val oldMenus = database.query("SELECT * FROM table_menu")
            oldMenus.moveToFirst()
            do {
                val circleMenu =  CircleMenu(
                    id = oldMenus.getInt(oldMenus.getColumnIndexOrThrow("id")),
                    title = oldMenus.getString(oldMenus.getColumnIndexOrThrow("title")),
                    items = oldMenus.getString(oldMenus.getColumnIndexOrThrow("items")).toCircleMenuItems()
                )
                newMenus.add(circleMenu)
            } while (oldMenus.moveToNext())
            database.execSQL("DELETE FROM table_menu")
            newMenus.forEach { circleMenu ->
                database.execSQL("INSERT INTO table_menu (id, title, items) VALUES (?, ?, ?)", arrayOf(circleMenu.id, circleMenu.title, DataBaseTypeConverter().fromCircleMenuItems(circleMenu.items)))
            }
        }

        private fun String.toCircleMenuItems(): List<CircleMenuItem> {
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson<List<String>>(this, type).map { it.toCircleMenuItem() }
        }

        private fun String.toCircleMenuItem(): CircleMenuItem {
            val circleMenuItemToSave = gson.fromJson(this, CircleMenuItemToSave::class.java)
            val offset = gson.fromJson(circleMenuItemToSave.offset, Offset::class.java)
            val image = circleMenuItemToSave.image.toCircleMenuImage()
            val action = circleMenuItemToSave.action.toCircleMenuAction()
            return CircleMenuItem(
                offset = offset,
                image = image,
                action = action
            )
        }

        private fun String.toCircleMenuImage(): CircleMenuImage {
            val circleMenuImageToSave = gson.fromJson(this, CircleMenuImageToSave::class.java)
            val classOfData = getClassOfImageData(circleMenuImageToSave.type)
            if (classOfData == DefaultImage::class.java) {
                val defaultImage = gson.fromJson(circleMenuImageToSave.data, DefaultImages::class.java)
                return DefaultImage(defaultImage)
            }
            return gson.fromJson(circleMenuImageToSave.data, classOfData) as CircleMenuImage
        }

        private fun getClassOfImageData(type: CircleMenuImageTypes): Class<*> {
            return when (type) {
                CircleMenuImageTypes.AppImage -> AppImage::class.java
                CircleMenuImageTypes.DefaultImage -> DefaultImage::class.java
                CircleMenuImageTypes.UserImage -> UserImage::class.java
            }
        }

        private fun String.toCircleMenuAction(): CircleMenuAction {
            val circleMenuActionToSave = gson.fromJson(this, CircleMenuActionToSave::class.java)
            val classOfData = getClassOfActionData(circleMenuActionToSave.type)
            return if (classOfData == null) {
                when (circleMenuActionToSave.type) {
                    CircleMenuActionTypes.OpenSettings -> OpenSettingsAction
                    CircleMenuActionTypes.FlashLightOn -> FlashLightOnAction
                    CircleMenuActionTypes.FlashLightOff -> FlashLightOffAction
                    CircleMenuActionTypes.ChangeFlashLightCondition -> ChangeFlashLightConditionAction
                    else -> OpenSettingsAction
                }
            } else {
                gson.fromJson(circleMenuActionToSave.data, classOfData) as CircleMenuAction
            }
        }

        private fun getClassOfActionData(type: CircleMenuActionTypes): Class<*>? {
            return when (type) {
                CircleMenuActionTypes.OpenCircleMenu -> OpenCircleMenuAction::class.java
                CircleMenuActionTypes.OpenApp -> OpenAppAction::class.java
                CircleMenuActionTypes.OpenUrl -> OpenUrlAction::class.java
                CircleMenuActionTypes.Call -> CallAction::class.java
                CircleMenuActionTypes.Dial -> DialAction::class.java
                else -> null
            }
        }

        private enum class CircleMenuActionTypes {
            OpenCircleMenu,
            OpenSettings,
            OpenApp,
            FlashLightOn,
            FlashLightOff,
            ChangeFlashLightCondition,
            Call,
            Dial,
            OpenUrl
        }

        private enum class CircleMenuImageTypes {
            AppImage, DefaultImage, UserImage
        }
        private class CircleMenuItemToSave {
            var offset: String = ""
            var image: String = ""
            var action: String = ""
        }

        private class CircleMenuActionToSave {
            var type: CircleMenuActionTypes = CircleMenuActionTypes.OpenSettings
            var data: String = ""
        }

        private class CircleMenuImageToSave {
            var type: CircleMenuImageTypes = CircleMenuImageTypes.DefaultImage
            var data: String = ""
        }
    }
}