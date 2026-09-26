package com.ekwabia.rhcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_records")
data class CalculationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wetBulb: Double,
    val dryBulb: Double,
    val pressure: Double,
    val relativeHumidity: Double,
    val timestamp: Long
)
