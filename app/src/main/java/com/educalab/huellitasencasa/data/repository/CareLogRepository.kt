package com.educalab.huellitasencasa.data.repository

import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.entity.CareSessionEntity
import com.educalab.huellitasencasa.data.local.entity.DailyCarePlanEntity
import com.educalab.huellitasencasa.data.local.entity.DailyCarePlanItemEntity
import com.educalab.huellitasencasa.domain.logic.PlannerValidator
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

    /** Guarda las tarjetas colocadas por el niño y devuelve el resultado de validación. */
    suspend fun submitPlan(plan: DailyCarePlanEntity, items: List<PlannerValidator.PlanItem>): PlannerValidator.ValidationResult {
        val result = PlannerValidator.validate(items)
        val entities = items.mapIndexed { index, item ->
            DailyCarePlanItemEntity(
                dailyCarePlanId = plan.id,
                slot = item.slot.name,
                careActionType = item.actionType.name,
                orderIndex = index,
                isCorrectPlacement = result.itemResults.getOrElse(index) { false }
            )
        }
        db.dailyCarePlanDao().replacePlanItems(plan.id, entities)
        if (result.isPlanApproved && !plan.completed) {
            db.dailyCarePlanDao().updatePlan(plan.copy(completed = true))
        }
        return result
    }

    suspend fun countCompletedPlans(petId: Long): Int = db.dailyCarePlanDao().countCompletedPlans(petId)
}
