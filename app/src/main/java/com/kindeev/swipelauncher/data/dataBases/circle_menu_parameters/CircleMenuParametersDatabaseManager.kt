package com.kindeev.swipelauncher.data.dataBases.circle_menu_parameters

import com.kindeev.swipelauncher.domain.database.CircleMenuParametersRepository
import com.kindeev.swipelauncher.domain.entities.circle_menu.parameters.CircleMenuParameters
import kotlinx.coroutines.flow.map

class CircleMenuParametersDatabaseManager(
    private val dao: CircleMenuParametersDao
): CircleMenuParametersRepository {

    override fun getAllFlow() = dao.getAllFlow().map {
        it.map { it.toCircleMenuParameters() }
    }

    override suspend fun getAll() = dao.getAll().map {
        it.toCircleMenuParameters()
    }

    override suspend fun getByItemsCount(itemsCount: Int): CircleMenuParameters? {
        return dao.getByItemsCount(itemsCount)?.toCircleMenuParameters()
    }

    override suspend fun insert(circleMenuParameters: CircleMenuParameters) {
        dao.insert(circleMenuParameters.toSCircleMenuParameters())
    }

    override suspend fun insert(circleMenuParameters: List<CircleMenuParameters>) {
        dao.insert(circleMenuParameters.map { it.toSCircleMenuParameters() })
    }

    override suspend fun delete(circleMenuParameters: CircleMenuParameters) {
        dao.delete(circleMenuParameters.toSCircleMenuParameters())
    }

    override suspend fun delete(circleMenuParameters: List<CircleMenuParameters>) {
        dao.delete(circleMenuParameters.map { it.toSCircleMenuParameters() })
    }

    private fun SCircleMenuParameters.toCircleMenuParameters(): CircleMenuParameters {
        return CircleMenuParameters(
            itemsCount = itemsCount,
            itemSize = itemSize,
            offsets = offsets
        )
    }

    private fun CircleMenuParameters.toSCircleMenuParameters(): SCircleMenuParameters {
        return SCircleMenuParameters(
            itemsCount = itemsCount,
            itemSize = itemSize,
            offsets = offsets
        )
    }
}