package com.kindeev.swipelauncher.data.dataBases.application_data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDataDao {

    @Query("SELECT * FROM table_application_data")
    fun getAllFlow(): Flow<List<SApplicationData>>

    @Query("SELECT * FROM table_application_data")
    fun getAll(): List<SApplicationData>

    @Insert(SApplicationData::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(applicationsData: List<SApplicationData>)

    @Insert(SApplicationData::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(applicationData: SApplicationData)

    @Query("DELETE FROM table_application_data WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    @Query("DELETE FROM table_application_data WHERE packageName IN (:packageNames)")
    suspend fun delete(packageNames: List<String>)
}