package com.kindeev.swipelauncher.data.dataBases.application_data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SApplicationData::class], version = 1)
@TypeConverters(ApplicationDataTypeConverter::class)
abstract class ApplicationDataDatabase : RoomDatabase() {

    abstract fun getDao(): ApplicationDataDao

    companion object {

        @Volatile
        private var INSTANCE: ApplicationDataDatabase? = null

        fun getDatabase(context: Context): ApplicationDataDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = ApplicationDataDatabase::class.java,
                    name = "application_data.db"
                )
                    .build()
            }
        }
    }
}