package com.educalab.huellitasencasa.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tarjeta del minijuego "Clasifica y Cuida": alimento correcto, incorrecto o situación de cuidado. */
@Entity(
    tableName = "food_items",
    indices = [Index("species_id")]
)
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "species_id") val speciesId: Long?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "icon_res") val iconRes: String,
    @ColumnInfo(name = "is_appropriate") val isAppropriate: Boolean,
    @ColumnInfo(name = "explanation") val explanation: String
)

/** Objeto del hogar disponible para decorar/equipar la casa de la mascota (drag & drop). */
@Entity(tableName = "home_items")
data class HomeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon_res") val iconRes: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "compatible_species_csv") val compatibleSpeciesCsv: String
)

/** Escenario "¿Qué harías?" sobre señales de bienestar animal. */
@Entity(
    tableName = "wellbeing_scenarios",
    indices = [Index("species_id")]
)
data class WellbeingScenarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "species_id") val speciesId: Long?,
    @ColumnInfo(name = "situation_text") val situationText: String,
    @ColumnInfo(name = "icon_res") val iconRes: String,
    /** Opciones separadas por "|" (2 a 4 opciones). */
    @ColumnInfo(name = "options_csv") val optionsCsv: String,
    @ColumnInfo(name = "correct_option_index") val correctOptionIndex: Int,
    @ColumnInfo(name = "explanation") val explanation: String,
    @ColumnInfo(name = "recommend_ask_adult") val recommendAskAdult: Boolean
)

/** Insignia coleccionable de cuidador. */
@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon_res") val iconRes: String,
    @ColumnInfo(name = "tier") val tier: Int
)

/** Objeto decorativo desbloqueable para el hogar. */
@Entity(tableName = "decorations")
data class DecorationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon_res") val iconRes: String,
    @ColumnInfo(name = "category") val category: String
)

/** Misión: objetivo con progreso y recompensa opcional (insignia y/o decoración). */
@Entity(
    tableName = "missions",
    foreignKeys = [
        ForeignKey(entity = BadgeEntity::class, parentColumns = ["id"], childColumns = ["reward_badge_id"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = DecorationEntity::class, parentColumns = ["id"], childColumns = ["reward_decoration_id"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("reward_badge_id"), Index("reward_decoration_id")]
)
data class MissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "target_count") val targetCount: Int,
    @ColumnInfo(name = "reward_badge_id") val rewardBadgeId: Long?,
    @ColumnInfo(name = "reward_decoration_id") val rewardDecorationId: Long?,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
    @ColumnInfo(name = "icon_res") val iconRes: String
)
