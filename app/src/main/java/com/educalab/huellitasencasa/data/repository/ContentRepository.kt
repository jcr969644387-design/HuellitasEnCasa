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
     * Tarjetas para "Clasifica y Cuida". Si el usuario ya completó una ronda (respondió al menos
     * [QUIZ_COOLDOWN_MILLIS]-recientemente tantas tarjetas como el tamaño de ronda disponible),
     * se devuelve una lista vacía para que la pantalla muestre "vuelve más tarde" en vez de
     * reiniciar el cuestionario cada vez que se sale y se vuelve a entrar. Mientras no se haya
     * completado la ronda, se excluyen las tarjetas ya respondidas recientemente para traer
     * variaciones.
     */
    suspend fun randomFoodCards(speciesId: Long, userProfileId: Long, count: Int = 10): List<FoodItemEntity> {
        val since = System.currentTimeMillis() - QUIZ_COOLDOWN_MILLIS
        val recent = db.foodAttemptDao().getRecentlyAttemptedItemIds(userProfileId, since).toSet()
        val fullPool = db.foodItemDao().getAllForSpecies(speciesId)
        val roundSize = minOf(count, fullPool.size)
        if (roundSize > 0 && recent.size >= roundSize) return emptyList()
        val freshPool = fullPool.filter { it.id !in recent }
        return freshPool.shuffled().take(count)
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
     * Escenarios de "¿Qué harías?", con el mismo criterio que [randomFoodCards]: si ya se
     * completó una ronda reciente se devuelve una lista vacía (la pantalla muestra "vuelve más
     * tarde" en vez de reiniciar el cuestionario), y mientras tanto se excluyen los escenarios
     * ya respondidos recientemente para traer variaciones.
     */
    suspend fun randomScenarios(speciesId: Long, userProfileId: Long, count: Int = 6): List<WellbeingScenarioEntity> {
        val since = System.currentTimeMillis() - QUIZ_COOLDOWN_MILLIS
        val recent = db.scenarioAttemptDao().getRecentlyAnsweredScenarioIds(userProfileId, since).toSet()
        val fullPool = db.wellbeingScenarioDao().getAllForSpecies(speciesId)
        val roundSize = minOf(count, fullPool.size)
        if (roundSize > 0 && recent.size >= roundSize) return emptyList()
        val freshPool = fullPool.filter { it.id !in recent }
        return freshPool.shuffled().take(count)
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
