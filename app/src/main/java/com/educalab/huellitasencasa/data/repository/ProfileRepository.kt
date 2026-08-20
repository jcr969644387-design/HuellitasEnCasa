package com.educalab.huellitasencasa.data.repository

import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val db: HuellitasDatabase) {

    fun observeProfiles(): Flow<List<UserProfileEntity>> = db.userProfileDao().observeAll()

    fun observeProfile(id: Long): Flow<UserProfileEntity?> = db.userProfileDao().observeById(id)

    suspend fun getProfile(id: Long): UserProfileEntity? = db.userProfileDao().getById(id)

    suspend fun hasAnyProfile(): Boolean = db.userProfileDao().count() > 0

    suspend fun createProfile(alias: String, avatarId: Int): Long {
        val cleanAlias = alias.trim().ifBlank { "Cuidador" }.take(18)
        return db.userProfileDao().insert(
            UserProfileEntity(
                alias = cleanAlias,
                avatarId = avatarId,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updatePreferences(profile: UserProfileEntity, soundEnabled: Boolean, hapticEnabled: Boolean) {
        db.userProfileDao().update(profile.copy(soundEnabled = soundEnabled, hapticEnabled = hapticEnabled))
    }

    suspend fun updateAlias(profile: UserProfileEntity, newAlias: String, newAvatarId: Int) {
        val cleanAlias = newAlias.trim().ifBlank { profile.alias }.take(18)
        db.userProfileDao().update(profile.copy(alias = cleanAlias, avatarId = newAvatarId))
    }
}
