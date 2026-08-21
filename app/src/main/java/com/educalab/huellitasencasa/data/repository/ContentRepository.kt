package com.educalab.huellitasencasa.data.repository

import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.entity.FoodAttemptEntity
import com.educalab.huellitasencasa.data.local.entity.FoodItemEntity
import com.educalab.huellitasencasa.data.local.entity.HomeChallengeEntity
import com.educalab.huellitasencasa.data.local.entity.HomeItemEntity
import com.educalab.huellitasencasa.data.local.entity.ScenarioAttemptEntity
import com.educalab.huellitasencasa.data.local.entity.WellbeingScenarioEntity
import com.educalab.huellitasencasa.domain.logic.ScenarioGrader

class ContentRepository(private val db: HuellitasDatabase) {

    // --- Alimentación / clasificación ---
    suspend fun randomFoodCards(speciesId: Long, count: Int = 10): List<FoodItemEntity> =
        db.foodItemDao().getRandomForSpecies(speciesId, count)

    /**
     * Alimentos realmente recomendados para la especie (categoría ALIMENTO_BUENO únicamente,
     * nunca las tarjetas de "situación de cuidado" que comparten la misma tabla). El agua
     * siempre aparece primero cuando está disponible; el resto se completa al azar.
     */
    suspend fun preferredFoodsFor(speciesId: Long, count: Int = 4): List<FoodItemEntity> {
        val pool = db.foodItemDao().getGoodFoodsForSpecies(speciesId)
        val water = pool.filter { it.iconRes == "food_agua_fresca" }.take(1)
        val solids = pool.filterNot { it.iconRes == "food_agua_fresca" }.shuffled()
        return (water + solids).take(count)
    }

    suspend fun recordFoodAttempt(userProfileId: Long, item: FoodItemEntity, petId: Long?, userSaidAppropriate: Boolean): Boolean {
        val wasCorrect = userSaidAppropriate == item.isAppropriate
        db.foodAttemptDao().insert(
            FoodAttemptEntity(
                userProfileId = userProfileId,
                foodItemId = item.id,
                virtualPetId = petId,
                wasCorrect = wasCorrect,
                timestamp = System.currentTimeMillis()
            )
        )
        return wasCorrect
    }

    suspend fun countCorrectFoodAttempts(userProfileId: Long): Int = db.foodAttemptDao().countCorrect(userProfileId)

    // --- Hogar de la mascota ---
    suspend fun homeItemsFor(speciesCode: String): List<HomeItemEntity> = db.homeItemDao().getCompatibleWith(speciesCode)

    /** Ids de los objetos del hogar ya colocados correctamente para esta mascota (persistente). */
    suspend fun completedHomeItemIds(petId: Long): Set<Long> =
        db.homeChallengeDao().getCorrectlyPlacedItemIds(petId).toSet()

    suspend fun recordHomePlacement(petId: Long, item: HomeItemEntity, placedCorrectly: Boolean) {
        db.homeChallengeDao().insert(
            HomeChallengeEntity(
                virtualPetId = petId,
                homeItemId = item.id,
                placedCorrectly = placedCorrectly,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // --- Señales de bienestar ---
    suspend fun randomScenarios(speciesId: Long, count: Int = 6): List<WellbeingScenarioEntity> =
        db.wellbeingScenarioDao().getRandomForSpecies(speciesId, count)

    suspend fun recordScenarioAttempt(
        userProfileId: Long,
        scenario: WellbeingScenarioEntity,
        petId: Long?,
        chosenOptionIndex: Int
    ): Boolean {
        val wasCorrect = ScenarioGrader.grade(scenario.correctOptionIndex, chosenOptionIndex)
        db.scenarioAttemptDao().insert(
            ScenarioAttemptEntity(
                userProfileId = userProfileId,
                scenarioId = scenario.id,
                virtualPetId = petId,
                chosenOptionIndex = chosenOptionIndex,
                wasCorrect = wasCorrect,
                timestamp = System.currentTimeMillis()
            )
        )
        return wasCorrect
    }

    suspend fun countCorrectScenarioAttempts(userProfileId: Long): Int = db.scenarioAttemptDao().countCorrect(userProfileId)
}
