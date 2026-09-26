package com.ekwabia.rhcalc.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: CalculationDao) {

    val allRecords: Flow<List<CalculationRecord>> = dao.getAll()

    suspend fun insert(record: CalculationRecord) = dao.insert(record)

    suspend fun delete(record: CalculationRecord) = dao.delete(record)

    suspend fun clear() = dao.clear()
}
