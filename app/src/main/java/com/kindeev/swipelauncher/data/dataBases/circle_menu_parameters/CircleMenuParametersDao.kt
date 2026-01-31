package com.kindeev.swipelauncher.data.dataBases.circle_menu_parameters

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CircleMenuParametersDao {

    @Query("SELECT * FROM table_circle_menu_parameters")
    fun getAllFlow(): Flow<List<SCircleMenuParameters>>

    @Query("SELECT * FROM table_circle_menu_parameters")
    suspend fun getAll(): List<SCircleMenuParameters>

    @Query("SELECT * FROM table_circle_menu_parameters WHERE itemsCount = :itemsCount")
    suspend fun getByItemsCount(itemsCount: Int): SCircleMenuParameters?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(circleMenuParameters: SCircleMenuParameters)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(circleMenuParameters: List<SCircleMenuParameters>)

    @Delete
    suspend fun delete(circleMenuParameters: SCircleMenuParameters)

    @Delete
    suspend fun delete(circleMenuParameters: List<SCircleMenuParameters>)
}