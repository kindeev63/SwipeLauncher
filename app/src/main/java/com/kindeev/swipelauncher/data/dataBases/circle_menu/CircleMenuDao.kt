package com.kindeev.swipelauncher.data.dataBases.circle_menu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CircleMenuDao {

    @Query("SELECT * FROM table_circle_menu")
    fun getAllFlow(): Flow<List<SCircleMenu>>

    @Query("SELECT * FROM table_circle_menu")
    suspend fun getAll(): List<SCircleMenu>

    @Query("SELECT * FROM table_circle_menu WHERE id = :id")
    suspend fun getById(id: Int): SCircleMenu?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(circleMenu: SCircleMenu)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(circleMenus: List<SCircleMenu>)

    @Query("DELETE FROM table_circle_menu WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)
}