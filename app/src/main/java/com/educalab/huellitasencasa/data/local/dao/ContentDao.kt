package com.educalab.huellitasencasa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educalab.huellitasencasa.data.local.entity.FoodAttemptEntity
import com.educalab.huellitasencasa.data.local.entity.FoodItemEntity
import com.educalab.huellitasencasa.data.local.entity.HomeChallengeEntity
import com.educalab.huellitasencasa.data.local.entity.HomeItemEntity
import com.educalab.huellitasencasa.data.local.entity.ScenarioAttemptEntity
import com.educalab.huellitasencasa.data.local.entity.WellbeingScenarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items ORDER BY id ASC")
    fun observeAll(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE species_id = :speciesId OR species_id IS NULL ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomForSpecies(speciesId: Long, limit: Int): List<FoodItemEntity>

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun count(): Int
}

@Dao
interface FoodAttemptDao {
    @Insert
    suspend fun insert(attempt: FoodAttemptEntity): Long

    @Query("SELECT COUNT(*) FROM food_attempts WHERE user_profile_id = :userProfileId AND was_correct = 1")
    suspend fun countCorrect(userProfileId: Long): Int

    @Query("SELECT COUNT(*) FROM food_attempts WHERE user_profile_id = :userProfileId")
    suspend fun countAll(userProfileId: Long): Int

    @Query("SELECT * FROM food_attempts WHERE user_profile_id = :userProfileId ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(userProfileId: Long, limit: Int = 10): Flow<List<FoodAttemptEntity>>
}

@Dao
interface HomeItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<HomeItemEntity>)

    @Query("SELECT * FROM home_items ORDER BY id ASC")
    fun observeAll(): Flow<List<HomeItemEntity>>

    @Query("SELECT * FROM home_items WHERE compatible_species_csv LIKE '%' || :speciesCode || '%' ORDER BY category ASC")
    suspend fun getCompatibleWith(speciesCode: String): List<HomeItemEntity>

    @Query("SELECT COUNT(*) FROM home_items")
    suspend fun count(): Int
}

@Dao
interface HomeChallengeDao {
    @Insert
    suspend fun insert(challenge: HomeChallengeEntity): Long

    @Query("SELECT COUNT(*) FROM home_challenges WHERE virtual_pet_id = :petId AND placed_correctly = 1")
    suspend fun countCorrectForPet(petId: Long): Int

    @Query("SELECT COUNT(*) FROM home_challenges WHERE virtual_pet_id = :petId")
    suspend fun countAllForPet(petId: Long): Int
}

@Dao
interface WellbeingScenarioDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(scenarios: List<WellbeingScenarioEntity>)

    @Query("SELECT * FROM wellbeing_scenarios ORDER BY id ASC")
    fun observeAll(): Flow<List<WellbeingScenarioEntity>>

    @Query("SELECT * FROM wellbeing_scenarios WHERE species_id = :speciesId OR species_id IS NULL ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomForSpecies(speciesId: Long, limit: Int): List<WellbeingScenarioEntity>

    @Query("SELECT * FROM wellbeing_scenarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WellbeingScenarioEntity?

    @Query("SELECT COUNT(*) FROM wellbeing_scenarios")
    suspend fun count(): Int
}

@Dao
interface ScenarioAttemptDao {
    @Insert
    suspend fun insert(attempt: ScenarioAttemptEntity): Long

    @Query("SELECT COUNT(*) FROM scenario_attempts WHERE user_profile_id = :userProfileId AND was_correct = 1")
    suspend fun countCorrect(userProfileId: Long): Int

    @Query("SELECT COUNT(*) FROM scenario_attempts WHERE user_profile_id = :userProfileId")
    suspend fun countAll(userProfileId: Long): Int

    @Query("SELECT * FROM scenario_attempts WHERE user_profile_id = :userProfileId AND was_correct = 0 ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMistakes(userProfileId: Long, limit: Int = 10): List<ScenarioAttemptEntity>
}
