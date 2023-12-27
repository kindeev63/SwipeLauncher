package com.kindeev.swipelauncher.domain

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppDao {

    @Query("SELECT * FROM table_menu")
    fun getAllCircleMenu(): LiveData<List<CircleMenu>>

    @Query("SELECT * FROM table_menu WHERE id = :id")
    fun getCircleMenu(id: Int): CircleMenu?

    @Insert(CircleMenu::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircleMenu(circleMenu: CircleMenu)

    @Insert(CircleMenu::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>)

    @Delete(CircleMenu::class)
    suspend fun deleteCircleMenu(circleMenu: CircleMenu)
}