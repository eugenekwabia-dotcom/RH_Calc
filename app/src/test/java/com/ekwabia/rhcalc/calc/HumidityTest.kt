package com.ekwabia.rhcalc.calc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HumidityTest {

    @Test
    fun `matches worked example from spec`() {
        val rh = Humidity.relativeHumidity(wetBulb = 18.0, dryBulb = 25.0, pressureHpa = 1013.25)
        assertEquals(50.0, rh, 0.5)
    }

    @Test
    fun `equal wet and dry bulb yields 100 percent`() {
        val rh = Humidity.relativeHumidity(wetBulb = 20.0, dryBulb = 20.0)
        assertEquals(100.0, rh, 0.01)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `wet bulb exceeding dry bulb throws`() {
        Humidity.relativeHumidity(wetBulb = 26.0, dryBulb = 25.0)
    }

    @Test
    fun `result is always clamped between 0 and 100`() {
        val rh = Humidity.relativeHumidity(wetBulb = -10.0, dryBulb = -10.0, pressureHpa = 1013.25)
        assertTrue(rh in 0.0..100.0)
    }
}
