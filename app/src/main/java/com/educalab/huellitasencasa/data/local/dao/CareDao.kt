package com.educalab.huellitasencasa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.educalab.huellitasencasa.data.local.entity.CareActionEntity
import com.educalab.huellitasencasa.data.local.entity.CareSessionEntity
import com.educalab.huellitasencasa.data.local.entity.DailyCarePlanEntity
import com.educalab.huellitasencasa.data.local.entity.DailyCarePlanItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CareActionDao {
    @Insert
    suspend fun insert(action: CareActionEntity): Long

    @Query("SELECT * FROM care_actions WHERE virtual_pet_id = :petId ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(petId: Long, limit: Int = 20): Flow<List<CareActionEntity>>

    @Query("SELECT COUNT(*) FROM care_actions WHERE virtual_pet_id = :petId AND action_type = :actionType")
    suspend fun countByType(petId: Long, actionType: String): Int

    @Query("SELECT COUNT(*) FROM care_actions WHERE virtual_pet_id = :petId")
    suspend fun countAll(petId: Long): Int
}

@Dao
interface CareSessionDao {
    @Insert
    suspend fun insert(session: CareSessionEntity): Long

    @Update
    suspend fun update(session: CareSessionEntity)

    @Query("SELECT * FROM care_sessions WHERE virtual_pet_id = :petId ORDER BY started_at DESC")
    fun observeForPet(petId: Long): Flow<List<CareSessionEntity>>

    @Query("SELECT COUNT(*) FROM care_sessions WHERE virtual_pet_id = :petId")
    suspend fun countForPet(petId: Long): Int

    @Query("SELECT AVG(wellbeing_at_end) FROM care_sessions WHERE virtual_pet_id = :petId")
    suspend fun averageWellbeing(petId: Long): Double?
}

@Dao
interface DailyCarePlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: DailyCarePlanEntity): Long

    @Update
    suspend fun updatePlan(plan: DailyCarePlanEntity)

    @Insert
    suspend fun insertItems(items: List<DailyCarePlanItemEntity>): List<Long>

    @Query("DELETE FROM daily_care_plan_items WHERE daily_care_plan_id = :planId")
    suspend fun clearItems(planId: Long)

    @Query("SELECT * FROM daily_care_plans WHERE virtual_pet_id = :petId AND date_epoch_day = :epochDay LIMIT 1")
    suspend fun getPlanForDay(petId: Long, epochDay: Long): DailyCarePlanEntity?

    @Query("SELECT * FROM daily_care_plan_items WHERE daily_care_plan_id = :planId ORDER BY order_index ASC")
    fun observeItems(planId: Long): Flow<List<DailyCarePlanItemEntity>>

    @Query("SELECT * FROM daily_care_plan_items WHERE daily_care_plan_id = :planId ORDER BY order_index ASC")
    suspend fun getItemsOnce(planId: Long): List<DailyCarePlanItemEntity>

    @Query("SELECT * FROM daily_care_plans WHERE virtual_pet_id = :petId ORDER BY date_epoch_day DESC")
    fun observePlansForPet(petId: Long): Flow<List<DailyCarePlanEntity>>

    @Query("SELECT COUNT(*) FROM daily_care_plans WHERE virtual_pet_id = :petId AND completed = 1")
    suspend fun countCompletedPlans(petId: Long): Int

    @Transaction
    suspend fun replacePlanItems(planId: Long, items: List<DailyCarePlanItemEntity>) {
        clearItems(planId)
        insertItems(items)
    }
}
