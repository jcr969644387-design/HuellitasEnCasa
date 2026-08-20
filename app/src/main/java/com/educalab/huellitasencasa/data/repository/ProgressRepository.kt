package com.educalab.huellitasencasa.data.repository

import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.entity.AcademyLessonProgressEntity
import com.educalab.huellitasencasa.data.local.entity.BadgeEntity
import com.educalab.huellitasencasa.data.local.entity.DecorationEntity
import com.educalab.huellitasencasa.data.local.entity.MissionCompletionEntity
import com.educalab.huellitasencasa.data.local.entity.MissionEntity
import com.educalab.huellitasencasa.data.local.entity.UnlockedDecorationEntity
import com.educalab.huellitasencasa.data.local.entity.UserBadgeEntity
import com.educalab.huellitasencasa.domain.logic.MissionEngine
import kotlinx.coroutines.flow.Flow

/** Recompensas nuevas obtenidas al registrar un evento de progreso, para mostrar feedback festivo. */
data class NewRewards(
    val newlyCompletedMissions: List<MissionEntity>,
    val newBadges: List<BadgeEntity>,
    val newDecorations: List<DecorationEntity>
)

class ProgressRepository(private val db: HuellitasDatabase) {

    fun observeMissions(): Flow<List<MissionEntity>> = db.missionDao().observeAll()
    fun observeCompletions(userProfileId: Long): Flow<List<MissionCompletionEntity>> = db.missionCompletionDao().observeForUser(userProfileId)
    fun observeBadges(): Flow<List<BadgeEntity>> = db.badgeDao().observeAll()
    fun observeUserBadges(userProfileId: Long): Flow<List<UserBadgeEntity>> = db.userBadgeDao().observeForUser(userProfileId)
    fun observeDecorations(): Flow<List<DecorationEntity>> = db.decorationDao().observeAll()
    fun observeUnlockedDecorations(userProfileId: Long): Flow<List<UnlockedDecorationEntity>> = db.unlockedDecorationDao().observeForUser(userProfileId)
    fun observeAcademyProgress(userProfileId: Long): Flow<List<AcademyLessonProgressEntity>> = db.academyLessonProgressDao().observeForUser(userProfileId)

    /**
     * Registra que ocurrió un evento real de progreso (por ejemplo "ALIMENTACION" tras alimentar
     * a la mascota, o "BIENESTAR" tras acertar un escenario). Actualiza el progreso de todas las
     * misiones de ese tipo aún no completadas y, si alguna se completa, entrega su recompensa real.
     */
    suspend fun registerEvent(userProfileId: Long, eventType: String, amount: Int = 1): NewRewards {
        val missions = db.missionDao().getByType(eventType)
        if (missions.isEmpty()) return NewRewards(emptyList(), emptyList(), emptyList())

        val missionDefs = missions.map { MissionEngine.MissionDef(it.id, it.type, it.targetCount) }
        val currentStates = missions.associate { m ->
            val existing = db.missionCompletionDao().getFor(userProfileId, m.id)
            m.id to MissionEngine.CompletionState(
                missionId = m.id,
                progressCount = existing?.progressCount ?: 0,
                completed = existing?.completed ?: false
            )
        }

        val updatedStates = MissionEngine.applyEvent(missionDefs, currentStates, eventType, amount)

        val newlyCompleted = mutableListOf<MissionEntity>()
        val newBadges = mutableListOf<BadgeEntity>()
        val newDecorations = mutableListOf<DecorationEntity>()

        for (state in updatedStates) {
            val previous = currentStates[state.missionId]
            db.missionCompletionDao().upsert(
                MissionCompletionEntity(
                    userProfileId = userProfileId,
                    missionId = state.missionId,
                    progressCount = state.progressCount,
                    completed = state.completed,
                    completedAt = if (state.completed) System.currentTimeMillis() else null
                )
            )
            val justCompleted = state.completed && previous?.completed != true
            if (justCompleted) {
                val mission = missions.first { it.id == state.missionId }
                newlyCompleted += mission
                mission.rewardBadgeId?.let { badgeId ->
                    if (!db.userBadgeDao().hasBadge(userProfileId, badgeId)) {
                        db.userBadgeDao().insert(UserBadgeEntity(userProfileId = userProfileId, badgeId = badgeId, earnedAt = System.currentTimeMillis()))
                        db.badgeDao().getById(badgeId)?.let { newBadges += it }
                    }
                }
                mission.rewardDecorationId?.let { decorationId ->
                    if (!db.unlockedDecorationDao().isUnlocked(userProfileId, decorationId)) {
                        db.unlockedDecorationDao().insert(UnlockedDecorationEntity(userProfileId = userProfileId, decorationId = decorationId, unlockedAt = System.currentTimeMillis()))
                        db.decorationDao().getById(decorationId)?.let { newDecorations += it }
                    }
                }
            }
        }

        // COLECCION missions (insignias/decoraciones/sesiones) se re-evalúan tras cada evento
        // porque su progreso depende de contadores agregados, no de eventType directo.
        return NewRewards(newlyCompleted, newBadges, newDecorations)
    }

    suspend fun markLessonViewed(userProfileId: Long, lessonCode: String): Boolean {
        val existing = db.academyLessonProgressDao().getFor(userProfileId, lessonCode)
        val alreadyCompleted = existing?.completed == true
        db.academyLessonProgressDao().upsert(
            AcademyLessonProgressEntity(
                id = existing?.id ?: 0,
                userProfileId = userProfileId,
                lessonCode = lessonCode,
                viewedCount = (existing?.viewedCount ?: 0) + 1,
                completed = true,
                completedAt = existing?.completedAt ?: System.currentTimeMillis()
            )
        )
        return !alreadyCompleted
    }

    suspend fun countCompletedLessons(userProfileId: Long): Int = db.academyLessonProgressDao().countCompleted(userProfileId)
    suspend fun countUserBadges(userProfileId: Long): Int = db.userBadgeDao().countForUser(userProfileId)
    suspend fun countUnlockedDecorations(userProfileId: Long): Int = db.unlockedDecorationDao().countForUser(userProfileId)
}
