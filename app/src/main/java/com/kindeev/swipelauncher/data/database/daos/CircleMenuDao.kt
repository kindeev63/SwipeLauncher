package com.kindeev.swipelauncher.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kindeev.swipelauncher.data.database.entities.circleMenu.CircleMenuTable
import kotlinx.coroutines.flow.Flow

@Dao
interface CircleMenuDao {

    @Query("SELECT id FROM circle_menu")
    suspend fun getCircleMenuIds(): List<Int>

    @Query("SELECT * FROM circle_menu")
    fun getAll(): Flow<List<CircleMenuTable>>

    @Insert(CircleMenuTable::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CircleMenuTable)

    @Insert(CircleMenuTable::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(entities: List<CircleMenuTable>)

    @Delete(CircleMenuTable::class)
    suspend fun deleteMany(entities: List<CircleMenuTable>)

}