package com.educalab.huellitasencasa.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Perfil del cuidador (niño/a). Nunca contiene datos personales reales:
 * solo un alias elegido libremente y un avatar local.
 */
@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "alias") val alias: String,
    @ColumnInfo(name = "avatar_id") val avatarId: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "sound_enabled") val soundEnabled: Boolean = true,
    @ColumnInfo(name = "haptic_enabled") val hapticEnabled: Boolean = true
)

/** Tabla de referencia (dato semilla) con las especies disponibles para adoptar. */
@Entity(tableName = "pet_species")
data class PetSpeciesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "icon_res") val iconRes: String
)

/**
 * Define, por especie, cómo se comporta cada necesidad (velocidad de descenso
 * en cada nueva sesión y un consejo educativo). Es dato semilla, no cambia con el uso.
 */
@Entity(
    tableName = "pet_need_definitions",
    foreignKeys = [
        ForeignKey(
            entity = PetSpeciesEntity::class,
            parentColumns = ["id"],
            childColumns = ["species_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["species_id", "need_type"], unique = true)]
)
data class PetNeedDefinitionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "species_id") val speciesId: Long,
    @ColumnInfo(name = "need_type") val needType: String,
    @ColumnInfo(name = "session_decay") val sessionDecay: Int,
    @ColumnInfo(name = "care_tip") val careTip: String
)

/**
 * Mascota virtual adoptada por un cuidador. Los seis indicadores viven siempre
 * en [0, 100]; nunca se permite que bajen de un piso mínimo protector (ver CareEngine),
 * de modo que la mascota jamás "muere" ni castiga la ausencia del jugador.
 */
@Entity(
    tableName = "virtual_pets",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_profile_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PetSpeciesEntity::class,
            parentColumns = ["id"],
            childColumns = ["species_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_profile_id"), Index("species_id")]
)
data class VirtualPetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_profile_id") val userProfileId: Long,
    @ColumnInfo(name = "species_id") val speciesId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "avatar_variant") val avatarVariant: Int,
    @ColumnInfo(name = "adopted_at") val adoptedAt: Long,
    @ColumnInfo(name = "feeding", defaultValue = "80") val feeding: Int = 80,
    @ColumnInfo(name = "hydration", defaultValue = "80") val hydration: Int = 80,
    @ColumnInfo(name = "hygiene", defaultValue = "80") val hygiene: Int = 80,
    @ColumnInfo(name = "activity_level", defaultValue = "80") val activityLevel: Int = 80,
    @ColumnInfo(name = "rest", defaultValue = "80") val rest: Int = 80,
    @ColumnInfo(name = "affection", defaultValue = "80") val affection: Int = 80,
    /** Pese al nombre de columna, guarda System.currentTimeMillis() (ver PetRepository/CareEngine). */
    @ColumnInfo(name = "last_session_epoch_day") val lastSessionEpochDay: Long,
    @ColumnInfo(name = "is_active", defaultValue = "1") val isActive: Boolean = true
)
