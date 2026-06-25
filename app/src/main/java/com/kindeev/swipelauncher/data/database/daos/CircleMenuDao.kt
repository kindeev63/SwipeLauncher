package com.kindeev.swipelauncher.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kindeev.swipelauncher.data.database.entities.circleMenu.CircleMenuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CircleMenuDao {

    @Query("SELECT * FROM circle_menu")
    fun getAll(): Flow<List<CircleMenuEntity>>

    @Insert(CircleMenuEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CircleMenuEntity)

    @Insert(CircleMenuEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(entities: List<CircleMenuEntity>)

    @Delete(CircleMenuEntity::class)
    suspend fun deleteMany(entities: List<CircleMenuEntity>)

}