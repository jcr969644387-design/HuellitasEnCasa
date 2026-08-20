package com.educalab.huellitasencasa.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Registro histórico de cada acción de cuidado real ejecutada sobre una mascota. */
@Entity(
    tableName = "care_actions",
    foreignKeys = [ForeignKey(entity = VirtualPetEntity::class, parentColumns = ["id"], childColumns = ["virtual_pet_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("virtual_pet_id"), Index("timestamp")]
)
data class CareActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "virtual_pet_id") val virtualPetId: Long,
    @ColumnInfo(name = "action_type") val actionType: String,
    @ColumnInfo(name = "need_type") val needType: String,
    @ColumnInfo(name = "delta") val delta: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

/** Resumen de una sesión de juego, usado para estadísticas y repaso. */
@Entity(
    tableName = "care_sessions",
    foreignKeys = [ForeignKey(entity = VirtualPetEntity::class, parentColumns = ["id"], childColumns = ["virtual_pet_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("virtual_pet_id")]
)
data class CareSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "virtual_pet_id") val virtualPetId: Long,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "ended_at") val endedAt: Long?,
    @ColumnInfo(name = "actions_count") val actionsCount: Int,
    @ColumnInfo(name = "wellbeing_at_end") val wellbeingAtEnd: Int
)

/** Intento del usuario en el minijuego de clasificación de alimentos/situaciones. */
@Entity(
    tableName = "food_attempts",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["user_profile_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = FoodItemEntity::class, parentColumns = ["id"], childColumns = ["food_item_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("user_profile_id"), Index("food_item_id")]
)
data class FoodAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_profile_id") val userProfileId: Long,
    @ColumnInfo(name = "food_item_id") val foodItemId: Long,
    @ColumnInfo(name = "virtual_pet_id") val virtualPetId: Long?,
    @ColumnInfo(name = "was_correct") val wasCorrect: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

/** Intento de colocar un objeto del hogar en el sitio adecuado (drag & drop). */
@Entity(
    tableName = "home_challenges",
    foreignKeys = [
        ForeignKey(entity = VirtualPetEntity::class, parentColumns = ["id"], childColumns = ["virtual_pet_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = HomeItemEntity::class, parentColumns = ["id"], childColumns = ["home_item_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("virtual_pet_id"), Index("home_item_id")]
)
data class HomeChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "virtual_pet_id") val virtualPetId: Long,
    @ColumnInfo(name = "home_item_id") val homeItemId: Long,
    @ColumnInfo(name = "placed_correctly") val placedCorrectly: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

/** Intento de respuesta a un escenario de bienestar. */
@Entity(
    tableName = "scenario_attempts",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["user_profile_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = WellbeingScenarioEntity::class, parentColumns = ["id"], childColumns = ["scenario_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("user_profile_id"), Index("scenario_id")]
)
data class ScenarioAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_profile_id") val userProfileId: Long,
    @ColumnInfo(name = "scenario_id") val scenarioId: Long,
    @ColumnInfo(name = "virtual_pet_id") val virtualPetId: Long?,
    @ColumnInfo(name = "chosen_option_index") val chosenOptionIndex: Int,
    @ColumnInfo(name = "was_correct") val wasCorrect: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

/** Plan de cuidado diario (mañana/tarde/noche) armado por el niño para una mascota. */
@Entity(
    tableName = "daily_care_plans",
    foreignKeys = [ForeignKey(entity = VirtualPetEntity::class, parentColumns = ["id"], childColumns = ["virtual_pet_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("virtual_pet_id"), Index(value = ["virtual_pet_id", "date_epoch_day"], unique = true)]
)
data class DailyCarePlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "virtual_pet_id") val virtualPetId: Long,
    @ColumnInfo(name = "date_epoch_day") val dateEpochDay: Long,
    @ColumnInfo(name = "completed") val completed: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Long
)

/** Cada tarjeta de acción colocada dentro de un plan diario. */
@Entity(
    tableName = "daily_care_plan_items",
    foreignKeys = [ForeignKey(entity = DailyCarePlanEntity::class, parentColumns = ["id"], childColumns = ["daily_care_plan_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("daily_care_plan_id")]
)
data class DailyCarePlanItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "daily_care_plan_id") val dailyCarePlanId: Long,
    @ColumnInfo(name = "slot") val slot: String,
    @ColumnInfo(name = "care_action_type") val careActionType: String,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    @ColumnInfo(name = "is_correct_placement") val isCorrectPlacement: Boolean
)

/** Progreso de un cuidador respecto a una misión concreta. */
@Entity(
    tableName = "mission_completions",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["user_profile_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MissionEntity::class, parentColumns = ["id"], childColumns = ["mission_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["user_profile_id", "mission_id"], unique = true)]
)
data class MissionCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_profile_id") val userProfileId: Long,
    @ColumnInfo(name = "mission_id") val missionId: Long,
    @ColumnInfo(name = "progress_count") val progressCount: Int,
    @ColumnInfo(name = "completed") val completed: Boolean,
    @ColumnInfo(name = "completed_at") val completedAt: Long?
)

/** Decoración que un cuidador ha desbloqueado realmente (por misión o logro). */
@Entity(
    tableName = "unlocked_decorations",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["user_profile_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DecorationEntity::class, parentColumns = ["id"], childColumns = ["decoration_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["user_profile_id", "decoration_id"], unique = true)]
)
data class UnlockedDecorationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_profile_id") val userProfileId: Long,
    @ColumnInfo(name = "decoration_id") val decorationId: Long,
    @ColumnInfo(name = "unlocked_at") val unlockedAt: Long
)

/**
 * Entidad adicional respecto al listado BASE mínimo de la especificación: registra qué
 * lecciones de la Academia de cuidado ha visto/completado cada cuidador. Es necesaria para
 * mostrar el estado real (disponible/completado) de cada lección y para que la misión
 * "Academia de oro" derive su progreso de acciones reales y no de una lista en memoria.
 */
@Entity(
    tableName = "academy_lesson_progress",
    foreignKeys = [ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["user_profile_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["user_profile_id", "lesson_code"], unique = true)]
)
data class AcademyLessonProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_profile_id") val userProfileId: Long,
    @ColumnInfo(name = "lesson_code") val lessonCode: String,
    @ColumnInfo(name = "viewed_count") val viewedCount: Int,
    @ColumnInfo(name = "completed") val completed: Boolean,
    @ColumnInfo(name = "completed_at") val completedAt: Long?
)

/** Insignia que un cuidador ha ganado realmente. */
@Entity(
    tableName = "user_badges",
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["user_profile_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = BadgeEntity::class, parentColumns = ["id"], childColumns = ["badge_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["user_profile_id", "badge_id"], unique = true)]
)
data class UserBadgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_profile_id") val userProfileId: Long,
    @ColumnInfo(name = "badge_id") val badgeId: Long,
    @ColumnInfo(name = "earned_at") val earnedAt: Long
)
