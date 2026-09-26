package com.ekwabia.rhcalc.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {

    @Insert
    suspend fun insert(record: CalculationRecord): Long

    @Query("SELECT * FROM calculation_records ORDER BY timestamp DESC")
    fun getAll(): Flow<List<CalculationRecord>>

    @Query("SELECT * FROM calculation_records ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): CalculationRecord?

    @Delete
    suspend fun delete(record: CalculationRecord)

    @Query("DELETE FROM calculation_records")
    suspend fun clear()
}
