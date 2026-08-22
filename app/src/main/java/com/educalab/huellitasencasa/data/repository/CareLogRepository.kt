package com.educalab.huellitasencasa.data.repository

import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.entity.CareSessionEntity
import com.educalab.huellitasencasa.data.local.entity.DailyCarePlanEntity
import com.educalab.huellitasencasa.data.local.entity.DailyCarePlanItemEntity
import com.educalab.huellitasencasa.domain.model.CareActionType
import com.educalab.huellitasencasa.domain.model.DaySlot
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneOffset

class CareLogRepository(private val db: HuellitasDatabase) {

    fun observeRecentActions(petId: Long, limit: Int = 20) = db.careActionDao().observeRecent(petId, limit)

    suspend fun startSession(petId: Long): Long =
        db.careSessionDao().insert(
            CareSessionEntity(virtualPetId = petId, startedAt = System.currentTimeMillis(), endedAt = null, actionsCount = 0, wellbeingAtEnd = 0)
        )

    suspend fun endSession(sessionId: Long, petId: Long, actionsCount: Int, wellbeingAtEnd: Int) {
        db.careSessionDao().update(
            CareSessionEntity(
                id = sessionId,
                virtualPetId = petId,
                startedAt = 0L,
                endedAt = System.currentTimeMillis(),
                actionsCount = actionsCount,
                wellbeingAtEnd = wellbeingAtEnd
            )
        )
    }

    suspend fun countSessions(petId: Long): Int = db.careSessionDao().countForPet(petId)

    private fun todayEpochDay(): Long = LocalDate.now(ZoneOffset.UTC).toEpochDay()

    suspend fun getOrCreateTodayPlan(petId: Long): DailyCarePlanEntity {
        val today = todayEpochDay()
        return db.dailyCarePlanDao().getPlanForDay(petId, today) ?: run {
            val id = db.dailyCarePlanDao().insertPlan(
                DailyCarePlanEntity(virtualPetId = petId, dateEpochDay = today, completed = false, createdAt = System.currentTimeMillis())
            )
            DailyCarePlanEntity(id = id, virtualPetId = petId, dateEpochDay = today, completed = false, createdAt = System.currentTimeMillis())
        }
    }

    fun observePlanItems(planId: Long): Flow<List<DailyCarePlanItemEntity>> = db.dailyCarePlanDao().observeItems(planId)

    /**
     * Guarda lo que el niño o niña elige hacer en cada franja del día (puede ser más de una
     * actividad por franja). Reemplaza el plan de hoy; el campo "is_correct_placement" ahora
     * significa "marcado como cumplido", no "colocado en el turno correcto": aquí no se
     * califica, se acompaña.
     */
    suspend fun savePlanSelections(petId: Long, selections: Map<DaySlot, Set<CareActionType>>): DailyCarePlanEntity {
        val plan = getOrCreateTodayPlan(petId)
        val previouslyDone = db.dailyCarePlanDao().getItemsOnce(plan.id)
            .filter { it.isCorrectPlacement }
            .map { it.slot }
            .toSet()
        var index = 0
        val entities = selections.entries.flatMap { (slot, actions) ->
            actions.map { action ->
                DailyCarePlanItemEntity(
                    dailyCarePlanId = plan.id,
                    slot = slot.name,
                    careActionType = action.name,
                    orderIndex = index++,
                    isCorrectPlacement = slot.name in previouslyDone
                )
            }
        }
        db.dailyCarePlanDao().replacePlanItems(plan.id, entities)
        return plan
    }

    /** Marca una franja del plan de hoy como cumplida. Devuelve true si con esto el día quedó completo. */
    suspend fun markPlanSlotDone(petId: Long, slot: DaySlot): Boolean {
        val plan = getOrCreateTodayPlan(petId)
        val items = db.dailyCarePlanDao().getItemsOnce(plan.id)
        val updated = items.map { if (it.slot == slot.name) it.copy(id = 0, isCorrectPlacement = true) else it.copy(id = 0) }
        db.dailyCarePlanDao().replacePlanItems(plan.id, updated)
        val allDone = updated.isNotEmpty() && updated.all { it.isCorrectPlacement }
        if (allDone && !plan.completed) {
            db.dailyCarePlanDao().updatePlan(plan.copy(completed = true))
        }
        return allDone
    }

    suspend fun countCompletedPlans(petId: Long): Int = db.dailyCarePlanDao().countCompletedPlans(petId)
}
