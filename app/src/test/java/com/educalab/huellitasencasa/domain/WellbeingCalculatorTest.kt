package com.educalab.huellitasencasa.domain

import com.educalab.huellitasencasa.domain.logic.CareEngine
import com.educalab.huellitasencasa.domain.logic.WellbeingCalculator
import com.educalab.huellitasencasa.domain.model.WellbeingLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WellbeingCalculatorTest {

    @Test
    fun `overallScore is the average of the six indicators`() {
        val indicators = CareEngine.PetIndicators(80, 80, 80, 80, 80, 80)
        assertEquals(80, WellbeingCalculator.overallScore(indicators))
    }

    @Test
    fun `overallScore rounds to the nearest integer`() {
        val indicators = CareEngine.PetIndicators(100, 100, 100, 100, 100, 0)
        // suma 500 / 6 = 83.33 -> redondea a 83
        assertEquals(83, WellbeingCalculator.overallScore(indicators))
    }

    @Test
    fun `levelFor returns EXCELENTE for high scores`() {
        assertEquals(WellbeingLevel.EXCELENTE, WellbeingCalculator.levelFor(90))
        assertEquals(WellbeingLevel.EXCELENTE, WellbeingCalculator.levelFor(80))
    }

    @Test
    fun `levelFor returns BIEN in the mid-high range`() {
        assertEquals(WellbeingLevel.BIEN, WellbeingCalculator.levelFor(60))
    }

    @Test
    fun `levelFor returns NECESITA_ATENCION in the mid-low range`() {
        assertEquals(WellbeingLevel.NECESITA_ATENCION, WellbeingCalculator.levelFor(35))
    }

    @Test
    fun `levelFor returns ATENCION_URGENTE for very low scores`() {
        assertEquals(WellbeingLevel.ATENCION_URGENTE, WellbeingCalculator.levelFor(10))
    }

    @Test
    fun `lowestIndicators returns the single minimum indicator`() {
        val indicators = CareEngine.PetIndicators(feeding = 30, hydration = 90, hygiene = 90, activity = 90, rest = 90, affection = 90)
        assertEquals(listOf("ALIMENTACION"), WellbeingCalculator.lowestIndicators(indicators))
    }

    @Test
    fun `lowestIndicators returns every indicator tied at the minimum`() {
        val indicators = CareEngine.PetIndicators(feeding = 40, hydration = 40, hygiene = 90, activity = 90, rest = 90, affection = 90)
        val lowest = WellbeingCalculator.lowestIndicators(indicators)
        assertTrue(lowest.containsAll(listOf("ALIMENTACION", "HIDRATACION")))
        assertEquals(2, lowest.size)
    }
}
