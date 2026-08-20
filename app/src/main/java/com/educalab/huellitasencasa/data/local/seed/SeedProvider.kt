package com.educalab.huellitasencasa.data.local.seed

import com.educalab.huellitasencasa.data.local.HuellitasDatabase

/**
 * Puebla la base de datos con contenido semilla la primera vez que se instala la app,
 * para que la instalación se sienta completa desde el primer momento (no 2-3 elementos
 * "de prueba"). Es idempotente: si ya hay especies cargadas, no vuelve a insertar nada.
 */
object SeedProvider {

    suspend fun seedIfNeeded(db: HuellitasDatabase) {
        if (db.petSpeciesDao().count() > 0) return

        // 1) Especies + definiciones de necesidades
        db.petSpeciesDao().insertAll(SeedSpecies.speciesList)
        val speciesByCode = db.petSpeciesDao().getAll().associate { it.code to it.id }
        val needDefs = speciesByCode.flatMap { (code, id) -> SeedSpecies.needDefinitionsFor(id, code) }
        db.petNeedDefinitionDao().insertAll(needDefs)

        // 2) Alimentos / situaciones (50)
        db.foodItemDao().insertAll(SeedFood.buildEntities(speciesByCode))

        // 3) Objetos del hogar
        db.homeItemDao().insertAll(SeedHomeItems.items)

        // 4) Escenarios de bienestar (30)
        db.wellbeingScenarioDao().insertAll(SeedScenarios.buildEntities(speciesByCode))

        // 5) Insignias y decoraciones (deben insertarse antes que las misiones, que las referencian)
        db.badgeDao().insertAll(SeedBadgesAndDecorations.badges)
        db.decorationDao().insertAll(SeedBadgesAndDecorations.decorations)
        val badgeIdByCode = db.badgeDao().getAll().associate { it.code to it.id }
        val decorationIdByCode = db.decorationDao().getAll().associate { it.code to it.id }

        // 6) Misiones (30), resolviendo recompensas por código
        db.missionDao().insertAll(SeedMissions.buildEntities(badgeIdByCode, decorationIdByCode))
    }
}
