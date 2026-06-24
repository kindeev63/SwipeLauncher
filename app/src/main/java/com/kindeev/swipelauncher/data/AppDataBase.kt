package com.kindeev.swipelauncher.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kindeev.swipelauncher.data.daos.ApplicationDataDao
import com.kindeev.swipelauncher.data.daos.CircleMenuDao
import com.kindeev.swipelauncher.data.daos.SettingsDao
import com.kindeev.swipelauncher.data.entities.applicationData.ApplicationDataEntity
import com.kindeev.swipelauncher.data.entities.circleMenu.CircleMenuEntity
import com.kindeev.swipelauncher.data.entities.settings.LauncherSettingsEntity
import com.kindeev.swipelauncher.data.typeConverters.ApplicationDataTypeConverter
import com.kindeev.swipelauncher.data.typeConverters.CircleMenuTypeConverter
import com.kindeev.swipelauncher.data.typeConverters.SettingsTypeConverter

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