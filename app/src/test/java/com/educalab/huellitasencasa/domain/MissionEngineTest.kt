package com.educalab.huellitasencasa.domain

import com.educalab.huellitasencasa.domain.logic.MissionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MissionEngineTest {

    private val feedingMission = MissionEngine.MissionDef(id = 1, type = "ALIMENTACION", targetCount = 5)
    private val hygieneMission = MissionEngine.MissionDef(id = 2, type = "HIGIENE", targetCount = 3)

    @Test
    fun `applyEvent increments progress for matching mission type`() {
        val result = MissionEngine.applyEvent(listOf(feedingMission), emptyMap(), "ALIMENTACION", amount = 1)
        assertEquals(1, result.single().progressCount)
        assertFalse(result.single().completed)
    }

    @Test
    fun `applyEvent ignores missions of a different type`() {
        val result = MissionEngine.applyEvent(listOf(hygieneMission), emptyMap(), "ALIMENTACION", amount = 1)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `applyEvent marks mission completed on reaching target`() {
        val current = mapOf(1L to MissionEngine.CompletionState(1, 4, false))
        val result = MissionEngine.applyEvent(listOf(feedingMission), current, "ALIMENTACION", amount = 1)
        assertTrue(result.single().completed)
        assertEquals(5, result.single().progressCount)
    }

    @Test
    fun `applyEvent never exceeds the target count`() {
        val current = mapOf(1L to MissionEngine.CompletionState(1, 4, false))
        val result = MissionEngine.applyEvent(listOf(feedingMission), current, "ALIMENTACION", amount = 10)
        assertEquals(5, result.single().progressCount)
    }

    @Test
    fun `applyEvent does not change an already completed mission`() {
        val current = mapOf(1L to MissionEngine.CompletionState(1, 5, true))
        val result = MissionEngine.applyEvent(listOf(feedingMission), current, "ALIMENTACION", amount = 3)
        assertEquals(5, result.single().progressCount)
        assertTrue(result.single().completed)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `applyEvent rejects non-positive amount`() {
        MissionEngine.applyEvent(listOf(feedingMission), emptyMap(), "ALIMENTACION", amount = 0)
    }

    @Test
    fun `progressRatio is bounded between 0 and 1`() {
        assertEquals(0.6f, MissionEngine.progressRatio(MissionEngine.CompletionState(1, 3, false), target = 5), 0.001f)
        assertEquals(1f, MissionEngine.progressRatio(MissionEngine.CompletionState(1, 99, true), target = 5), 0.001f)
        assertEquals(0f, MissionEngine.progressRatio(MissionEngine.CompletionState(1, 0, false), target = 0), 0.001f)
    }
}
