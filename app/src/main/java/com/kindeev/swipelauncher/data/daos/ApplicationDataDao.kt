package com.kindeev.swipelauncher.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kindeev.swipelauncher.data.entities.applicationData.ApplicationDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDataDao {

    @Query("SELECT * FROM application_data")
    fun getAll(): Flow<List<ApplicationDataEntity>>

    @Insert(ApplicationDataEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ApplicationDataEntity)

    @Insert(ApplicationDataEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(entities: List<ApplicationDataEntity>)

    @Delete(ApplicationDataEntity::class)
    suspend fun delete(entity: ApplicationDataEntity)

    @Delete(ApplicationDataEntity::class)
    suspend fun deleteMany(entities: List<ApplicationDataEntity>)

    @Query("DELETE FROM application_data WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
}