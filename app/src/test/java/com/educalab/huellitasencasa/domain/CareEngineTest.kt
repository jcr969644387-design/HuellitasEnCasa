package com.educalab.huellitasencasa.domain

import com.educalab.huellitasencasa.domain.logic.CareEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareEngineTest {

    @Test
    fun `applyAction clamps above 100`() {
        assertEquals(100, CareEngine.applyAction(95, 20))
    }

    @Test
    fun `applyAction clamps below 0`() {
        assertEquals(0, CareEngine.applyAction(5, -20))
    }

    @Test
    fun `applyAction adds delta within range`() {
        assertEquals(70, CareEngine.applyAction(50, 20))
    }

    @Test
    fun `clamp never returns value outside 0-100`() {
        assertEquals(0, CareEngine.clamp(-500))
        assertEquals(100, CareEngine.clamp(500))
        assertEquals(42, CareEngine.clamp(42))
    }

    @Test
    fun `applySessionDecay never drops below protected floor`() {
        val result = CareEngine.applySessionDecay(currentValue = 20, maxDecay = 50)
        assertTrue("El resultado ($result) no debe bajar del piso protector", result >= CareEngine.PROTECTED_FLOOR)
    }

    @Test
    fun `applySessionDecay applies normal decay above the floor`() {
        assertEquals(70, CareEngine.applySessionDecay(currentValue = 78, maxDecay = 8))
    }

    @Test
    fun `applySessionDecay with negative maxDecay does not increase value`() {
        val result = CareEngine.applySessionDecay(currentValue = 50, maxDecay = -10)
        assertEquals(50, result)
    }

    @Test
    fun `shouldApplyDecay is true only once the real-time interval has elapsed`() {
        val lastSession = 1_000_000L
        assertFalse(CareEngine.shouldApplyDecay(lastSessionAtMillis = lastSession, nowMillis = lastSession + 1_000L))
        assertFalse(
            CareEngine.shouldApplyDecay(
                lastSessionAtMillis = lastSession,
                nowMillis = lastSession + CareEngine.DECAY_INTERVAL_MILLIS - 1
            )
        )
        assertTrue(
            CareEngine.shouldApplyDecay(
                lastSessionAtMillis = lastSession,
                nowMillis = lastSession + CareEngine.DECAY_INTERVAL_MILLIS
            )
        )
    }

    @Test
    fun `applySessionDecayToAll decays every one of the six indicators`() {
        val indicators = CareEngine.PetIndicators(80, 80, 80, 80, 80, 80)
        val result = CareEngine.applySessionDecayToAll(indicators, maxDecay = 10)
        assertEquals(70, result.feeding)
        assertEquals(70, result.hydration)
        assertEquals(70, result.hygiene)
        assertEquals(70, result.activity)
        assertEquals(70, result.rest)
        assertEquals(70, result.affection)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `PetIndicators rejects out-of-range values`() {
        CareEngine.PetIndicators(feeding = 150, hydration = 50, hygiene = 50, activity = 50, rest = 50, affection = 50)
    }
}
