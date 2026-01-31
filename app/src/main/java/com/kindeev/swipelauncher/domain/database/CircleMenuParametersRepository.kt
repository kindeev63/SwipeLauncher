package com.kindeev.swipelauncher.domain.database

import com.kindeev.swipelauncher.domain.entities.circle_menu.parameters.CircleMenuParameters
import kotlinx.coroutines.flow.Flow

interface CircleMenuParametersRepository {

    fun getAllFlow(): Flow<List<CircleMenuParameters>>

    suspend fun getAll(): List<CircleMenuParameters>

    suspend fun getByItemsCount(itemsCount: Int): CircleMenuParameters?

    suspend fun insert(circleMenuParameters: CircleMenuParameters)

    suspend fun insert(circleMenuParameters: List<CircleMenuParameters>)

    suspend fun delete(circleMenuParameters: CircleMenuParameters)

    suspend fun delete(circleMenuParameters: List<CircleMenuParameters>)
}