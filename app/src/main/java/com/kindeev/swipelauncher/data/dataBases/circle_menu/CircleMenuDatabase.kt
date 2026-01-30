package com.kindeev.swipelauncher.data.dataBases.circle_menu

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SCircleMenu::class], version = 1)
@TypeConverters(CircleMenuTypeConverter::class)
abstract class CircleMenuDatabase : RoomDatabase() {

    abstract fun getDao(): CircleMenuDao

    companion object {

        @Volatile
        private var INSTANCE: CircleMenuDatabase? = null

        fun getDatabase(context: Context): CircleMenuDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = CircleMenuDatabase::class.java,
                    name = "circle_menu.db"
                )
                    .build()
            }
        }
    }
}