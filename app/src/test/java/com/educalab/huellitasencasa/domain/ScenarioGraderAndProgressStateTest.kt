package com.educalab.huellitasencasa.domain

import com.educalab.huellitasencasa.domain.logic.ProgressStateResolver
import com.educalab.huellitasencasa.domain.logic.ScenarioGrader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenarioGraderTest {
    @Test
    fun `grade returns true when the chosen option matches the correct one`() {
        assertTrue(ScenarioGrader.grade(correctOptionIndex = 1, chosenOptionIndex = 1))
    }

    @Test
    fun `grade returns false when the chosen option does not match`() {
        assertFalse(ScenarioGrader.grade(correctOptionIndex = 1, chosenOptionIndex = 2))
    }
}

class ProgressStateResolverTest {

    @Test
    fun `resolve returns BLOQUEADO when not unlocked regardless of attempts`() {
        val state = ProgressStateResolver.resolve(isUnlocked = false, attemptsCount = 10, correctCount = 10, masteryThreshold = 5)
        assertEquals(ProgressStateResolver.State.BLOQUEADO, state)
    }

    @Test
    fun `resolve returns DISPONIBLE for an unlocked module with no attempts`() {
        val state = ProgressStateResolver.resolve(isUnlocked = true, attemptsCount = 0, correctCount = 0, masteryThreshold = 5)
        assertEquals(ProgressStateResolver.State.DISPONIBLE, state)
    }

    @Test
    fun `resolve returns INICIADO when attempted but nothing correct yet`() {
        val state = ProgressStateResolver.resolve(isUnlocked = true, attemptsCount = 2, correctCount = 0, masteryThreshold = 5)
        assertEquals(ProgressStateResolver.State.INICIADO, state)
    }

    @Test
    fun `resolve returns COMPLETADO with partial correct progress`() {
        val state = ProgressStateResolver.resolve(isUnlocked = true, attemptsCount = 3, correctCount = 2, masteryThreshold = 5)
        assertEquals(ProgressStateResolver.State.COMPLETADO, state)
    }

    @Test
    fun `resolve returns DOMINADO once the mastery threshold is reached`() {
        val state = ProgressStateResolver.resolve(isUnlocked = true, attemptsCount = 6, correctCount = 5, masteryThreshold = 5)
        assertEquals(ProgressStateResolver.State.DOMINADO, state)
    }
}
