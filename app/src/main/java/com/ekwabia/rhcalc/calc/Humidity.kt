package com.ekwabia.rhcalc.calc

object Humidity {

    private const val A = 6.62e-4
    const val STANDARD_PRESSURE_HPA = 1013.25

    private fun es(t: Double): Double =
        6.112 * Math.exp(17.62 * t / (243.12 + t))

    /**
     * @throws IllegalArgumentException if wet-bulb exceeds dry-bulb.
     */
    fun relativeHumidity(
        wetBulb: Double,
        dryBulb: Double,
        pressureHpa: Double = STANDARD_PRESSURE_HPA
    ): Double {
        require(wetBulb <= dryBulb) {
            "Wet-bulb temperature cannot exceed dry-bulb temperature."
        }
        val e = es(wetBulb) - A * pressureHpa * (dryBulb - wetBulb)
        val rh = 100.0 * e / es(dryBulb)
        return rh.coerceIn(0.0, 100.0)
    }
}
