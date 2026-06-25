package com.kindeev.swipelauncher.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kindeev.swipelauncher.data.database.daos.ApplicationDataDao
import com.kindeev.swipelauncher.data.database.daos.CircleMenuDao
import com.kindeev.swipelauncher.data.database.daos.SettingsDao
import com.kindeev.swipelauncher.data.database.entities.applicationData.ApplicationDataEntity
import com.kindeev.swipelauncher.data.database.entities.circleMenu.CircleMenuEntity
import com.kindeev.swipelauncher.data.database.entities.settings.LauncherSettingsEntity
import com.kindeev.swipelauncher.data.database.typeConverters.ApplicationDataTypeConverter
import com.kindeev.swipelauncher.data.database.typeConverters.CircleMenuTypeConverter
import com.kindeev.swipelauncher.data.database.typeConverters.SettingsTypeConverter

@Database(
    entities = [
        CircleMenuEntity::class,
        LauncherSettingsEntity::class,
        ApplicationDataEntity::class
    ],
    version = 1
)
@TypeConverters(
    CircleMenuTypeConverter::class,
    SettingsTypeConverter::class,
    ApplicationDataTypeConverter::class
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun circleMenuDao(): CircleMenuDao
    abstract fun settingsDao(): SettingsDao
    abstract fun applicationDataDao(): ApplicationDataDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDataBase::class.java,
                    name = "database.db"
                )
                    .build()
            }
        }
    }
}