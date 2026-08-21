package com.kindeev.swipelauncher.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kindeev.swipelauncher.data.database.daos.CircleMenuDao
import com.kindeev.swipelauncher.data.database.daos.SettingsDao
import com.kindeev.swipelauncher.data.database.entities.circleMenu.CircleMenuTable
import com.kindeev.swipelauncher.data.database.entities.settings.LauncherSettingsTable
import com.kindeev.swipelauncher.data.database.typeConverters.CircleMenuTypeConverter
import com.kindeev.swipelauncher.data.database.typeConverters.SettingsTypeConverter

@Database(
    entities = [
        CircleMenuTable::class,
        LauncherSettingsTable::class,
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    CircleMenuTypeConverter::class,
    SettingsTypeConverter::class,
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun circleMenuDao(): CircleMenuDao
    abstract fun settingsDao(): SettingsDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDataBase::class.java,
                    name = "database.db"
                )
                    .fallbackToDestructiveMigrationFrom(false, 1, 2, 3)
                    .build().apply {
                        INSTANCE = this
                    }
            }
        }
    }
}