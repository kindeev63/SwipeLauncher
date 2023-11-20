package com.kindeev.swipelauncher.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kindeev.swipelauncher.domain.AppDao
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.DataBaseTypesConverter

@Database(entities = [CircleMenu::class], version = 1)
@TypeConverters(DataBaseTypesConverter::class)
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
                ).build()
            }
        }

    }
}