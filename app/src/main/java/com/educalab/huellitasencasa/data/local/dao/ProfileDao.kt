package com.educalab.huellitasencasa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.educalab.huellitasencasa.data.local.entity.PetNeedDefinitionEntity
import com.educalab.huellitasencasa.data.local.entity.PetSpeciesEntity
import com.educalab.huellitasencasa.data.local.entity.UserProfileEntity
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(profile: UserProfileEntity): Long

    @Update
    suspend fun update(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles ORDER BY id ASC")
    fun observeAll(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profiles ORDER BY id ASC LIMIT 1")
    suspend fun getFirstProfile(): UserProfileEntity?

    @Query("SELECT COUNT(*) FROM user_profiles")
    suspend fun count(): Int

    @Query("DELETE FROM user_profiles WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface PetSpeciesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(species: List<PetSpeciesEntity>)

    @Query("SELECT * FROM pet_species ORDER BY id ASC")
    fun observeAll(): Flow<List<PetSpeciesEntity>>

    @Query("SELECT * FROM pet_species ORDER BY id ASC")
    suspend fun getAll(): List<PetSpeciesEntity>

    @Query("SELECT * FROM pet_species WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PetSpeciesEntity?

    @Query("SELECT COUNT(*) FROM pet_species")
    suspend fun count(): Int
}

@Dao
interface PetNeedDefinitionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(defs: List<PetNeedDefinitionEntity>)

    @Query("SELECT * FROM pet_need_definitions WHERE species_id = :speciesId")
    suspend fun getForSpecies(speciesId: Long): List<PetNeedDefinitionEntity>

    @Query("SELECT COUNT(*) FROM pet_need_definitions")
    suspend fun count(): Int
}

@Dao
interface VirtualPetDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pet: VirtualPetEntity): Long

    @Update
    suspend fun update(pet: VirtualPetEntity)

    @Query("SELECT * FROM virtual_pets WHERE user_profile_id = :userProfileId ORDER BY adopted_at ASC")
    fun observeForUser(userProfileId: Long): Flow<List<VirtualPetEntity>>

    @Query("SELECT * FROM virtual_pets WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<VirtualPetEntity?>

    @Query("SELECT * FROM virtual_pets WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): VirtualPetEntity?

    @Query("SELECT * FROM virtual_pets WHERE user_profile_id = :userProfileId AND is_active = 1 ORDER BY adopted_at ASC LIMIT 1")
    suspend fun getActiveForUser(userProfileId: Long): VirtualPetEntity?

    @Query("SELECT COUNT(*) FROM virtual_pets WHERE user_profile_id = :userProfileId")
    suspend fun countForUser(userProfileId: Long): Int
}
