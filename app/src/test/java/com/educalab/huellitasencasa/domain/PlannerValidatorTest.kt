package com.educalab.huellitasencasa.domain

import com.educalab.huellitasencasa.domain.logic.PlannerValidator
import com.educalab.huellitasencasa.domain.model.CareActionType
import com.educalab.huellitasencasa.domain.model.DaySlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlannerValidatorTest {

    @Test
    fun `feeding is correct in the morning`() {
        assertTrue(PlannerValidator.isPlacementCorrect(DaySlot.MANANA, CareActionType.ALIMENTAR))
    }

    @Test
    fun `feeding is incorrect at night is false because night is also allowed`() {
        // ALIMENTAR está permitido en mañana y noche, no en tarde.
        assertFalse(PlannerValidator.isPlacementCorrect(DaySlot.TARDE, CareActionType.ALIMENTAR))
        assertTrue(PlannerValidator.isPlacementCorrect(DaySlot.NOCHE, CareActionType.ALIMENTAR))
    }

    @Test
    fun `resting only makes sense at night`() {
        assertTrue(PlannerValidator.isPlacementCorrect(DaySlot.NOCHE, CareActionType.DEJAR_DESCANSAR))
        assertFalse(PlannerValidator.isPlacementCorrect(DaySlot.MANANA, CareActionType.DEJAR_DESCANSAR))
    }

    @Test
    fun `water and affection are valid in any slot`() {
        DaySlot.entries.forEach { slot ->
            assertTrue(PlannerValidator.isPlacementCorrect(slot, CareActionType.DAR_AGUA))
            assertTrue(PlannerValidator.isPlacementCorrect(slot, CareActionType.DAR_CARINO))
        }
    }

    @Test
    fun `validate on an empty plan returns zero totals`() {
        val result = PlannerValidator.validate(emptyList())
        assertEquals(0, result.totalCount)
        assertFalse(result.isPlanApproved)
    }

    @Test
    fun `validate computes correct ratio for a mixed plan`() {
        val items = listOf(
            PlannerValidator.PlanItem(DaySlot.MANANA, CareActionType.ALIMENTAR), // correcto
            PlannerValidator.PlanItem(DaySlot.NOCHE, CareActionType.JUGAR)       // incorrecto (JUGAR solo tarde)
        )
        val result = PlannerValidator.validate(items)
        assertEquals(1, result.correctCount)
        assertEquals(2, result.totalCount)
        assertEquals(0.5f, result.scoreRatio, 0.001f)
        assertFalse(result.isPlanApproved)
    }

    @Test
    fun `a plan with 80 percent or more correct is approved`() {
        val items = listOf(
            PlannerValidator.PlanItem(DaySlot.MANANA, CareActionType.ALIMENTAR),
            PlannerValidator.PlanItem(DaySlot.TARDE, CareActionType.JUGAR),
            PlannerValidator.PlanItem(DaySlot.NOCHE, CareActionType.DEJAR_DESCANSAR),
            PlannerValidator.PlanItem(DaySlot.MANANA, CareActionType.DAR_AGUA),
            PlannerValidator.PlanItem(DaySlot.TARDE, CareActionType.CEPILLAR)
        )
        val result = PlannerValidator.validate(items)
        assertEquals(5, result.correctCount)
        assertTrue(result.isPlanApproved)
    }
}
