package com.kindeev.swipelauncher.data.dataBases.circle_menu_parameters

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SCircleMenuParameters::class], version = 1)
@TypeConverters(CircleMenuParametersTypeConverter::class)
abstract class CircleMenuParametersDatabase : RoomDatabase() {

    abstract fun getDao(): CircleMenuParametersDao

    companion object {

        @Volatile
        private var INSTANCE: CircleMenuParametersDatabase? = null

        fun getDatabase(context: Context): CircleMenuParametersDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = CircleMenuParametersDatabase::class.java,
                    name = "circle_menu_parameters.db"
                )
                    .build()
            }
        }
    }
}