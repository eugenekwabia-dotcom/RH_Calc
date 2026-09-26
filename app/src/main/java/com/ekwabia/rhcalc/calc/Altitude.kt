package com.ekwabia.rhcalc.calc

object Altitude {

    /** Approximate altitude (m) above sea level, assuming standard atmosphere. */
    fun fromPressure(pressureHpa: Double, seaLevelHpa: Double = Humidity.STANDARD_PRESSURE_HPA): Double =
        44330.0 * (1.0 - Math.pow(pressureHpa / seaLevelHpa, 1.0 / 5.255))
}
