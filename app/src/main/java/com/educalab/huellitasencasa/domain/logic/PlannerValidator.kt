package com.educalab.huellitasencasa.domain.logic

import com.educalab.huellitasencasa.domain.model.CareActionType
import com.educalab.huellitasencasa.domain.model.DaySlot

/**
 * Valida un plan diario armado por el niño (mañana/tarde/noche). En vez de exigir un único
 * orden "correcto" rígido, cada tipo de acción tiene una o varias franjas horarias adecuadas;
 * el plan se puntúa por proporción de tarjetas bien ubicadas, lo que permite feedback graduado
 * y varias soluciones válidas, más realista que un examen de opción única.
 */
object PlannerValidator {

    private val allowedSlots: Map<CareActionType, Set<DaySlot>> = mapOf(
        CareActionType.ALIMENTAR to setOf(DaySlot.MANANA, DaySlot.NOCHE),
        CareActionType.DAR_AGUA to setOf(DaySlot.MANANA, DaySlot.TARDE, DaySlot.NOCHE),
        CareActionType.CEPILLAR to setOf(DaySlot.TARDE),
        CareActionType.LIMPIAR_ESPACIO to setOf(DaySlot.MANANA, DaySlot.TARDE),
        CareActionType.JUGAR to setOf(DaySlot.TARDE),
        CareActionType.PASEAR to setOf(DaySlot.MANANA, DaySlot.TARDE),
        CareActionType.DEJAR_DESCANSAR to setOf(DaySlot.NOCHE),
        CareActionType.DAR_CARINO to setOf(DaySlot.MANANA, DaySlot.TARDE, DaySlot.NOCHE)
    )

    data class PlanItem(val slot: DaySlot, val actionType: CareActionType)

    data class ValidationResult(
        val itemResults: List<Boolean>,
        val correctCount: Int,
        val totalCount: Int,
        val scoreRatio: Float
    ) {
        /** Se considera un plan "completo y correcto" si acierta el 80% o más de las tarjetas. */
        val isPlanApproved: Boolean get() = totalCount > 0 && scoreRatio >= 0.8f
    }

    fun isPlacementCorrect(slot: DaySlot, actionType: CareActionType): Boolean =
        allowedSlots[actionType]?.contains(slot) ?: false

    fun validate(items: List<PlanItem>): ValidationResult {
        if (items.isEmpty()) return ValidationResult(emptyList(), 0, 0, 0f)
        val results = items.map { isPlacementCorrect(it.slot, it.actionType) }
        val correct = results.count { it }
        return ValidationResult(
            itemResults = results,
            correctCount = correct,
            totalCount = items.size,
            scoreRatio = correct.toFloat() / items.size.toFloat()
        )
    }

    fun tipFor(actionType: CareActionType): String {
        val slots = allowedSlots[actionType].orEmpty().joinToString(" o ") {
            when (it) {
                DaySlot.MANANA -> "la mañana"
                DaySlot.TARDE -> "la tarde"
                DaySlot.NOCHE -> "la noche"
            }
        }
        return "Esta tarjeta encaja mejor en $slots."
    }
}
