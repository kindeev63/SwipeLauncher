package com.kindeev.swipelauncher.domain.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.dataBase.typeConverter.CircleMenuTypeConverter
import com.kindeev.swipelauncher.domain.dataBase.typeConverter.SettingsTypeConverter

@Database(entities = [CircleMenu::class, SettingData::class, ApplicationData::class], version = 3)
@TypeConverters(SettingsTypeConverter::class, CircleMenuTypeConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getDao(): AppDao
    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDataBase::class.java,
                    name = "swipe_launcher.db"
                )
                    .addMigrations(Migrations.Migration_1_3.MIGRATION_1_3, Migrations.Migration_2_3.MIGRATION_2_3)
                    .build()
            }
        }
    }
}