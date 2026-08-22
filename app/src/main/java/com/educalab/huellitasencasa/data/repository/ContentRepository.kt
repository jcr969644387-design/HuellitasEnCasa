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

    companion object {
        /** Tiempo real que debe pasar antes de que una tarjeta/escenario ya visto pueda repetirse. */
        private const val QUIZ_COOLDOWN_MILLIS = 6L * 60L * 60L * 1000L
    }

    // --- Alimentación / clasificación ---
    /**
     * Tarjetas para "Clasifica y Cuida", evitando repetir (durante [QUIZ_COOLDOWN_MILLIS])
     * cualquier tarjeta que este perfil ya haya respondido, sin importar si acertó o no. Así,
     * salir y volver a entrar trae variaciones en vez de la misma ronda de siempre. Si el
     * enfriamiento dejara muy pocas tarjetas disponibles, se usa el mazo completo para no
     * mostrar una ronda vacía o demasiado corta.
     */
    suspend fun randomFoodCards(speciesId: Long, userProfileId: Long, count: Int = 10): List<FoodItemEntity> {
        val since = System.currentTimeMillis() - QUIZ_COOLDOWN_MILLIS
        val recent = db.foodAttemptDao().getRecentlyAttemptedItemIds(userProfileId, since).toSet()
        val fullPool = db.foodItemDao().getAllForSpecies(speciesId)
        val freshPool = fullPool.filter { it.id !in recent }
        val pool = if (freshPool.size >= count) freshPool else fullPool
        return pool.shuffled().take(count)
    }

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
    /**
     * Escenarios de "¿Qué harías?", con el mismo enfriamiento de [QUIZ_COOLDOWN_MILLIS] que la
     * comida: evita repetir lo ya respondido recientemente y trae variaciones al volver a entrar.
     */
    suspend fun randomScenarios(speciesId: Long, userProfileId: Long, count: Int = 6): List<WellbeingScenarioEntity> {
        val since = System.currentTimeMillis() - QUIZ_COOLDOWN_MILLIS
        val recent = db.scenarioAttemptDao().getRecentlyAnsweredScenarioIds(userProfileId, since).toSet()
        val fullPool = db.wellbeingScenarioDao().getAllForSpecies(speciesId)
        val freshPool = fullPool.filter { it.id !in recent }
        val pool = if (freshPool.size >= count) freshPool else fullPool
        return pool.shuffled().take(count)
    }

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
