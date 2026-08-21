package com.educalab.huellitasencasa.data.repository

import com.educalab.huellitasencasa.data.local.HuellitasDatabase
import com.educalab.huellitasencasa.data.local.entity.CareActionEntity
import com.educalab.huellitasencasa.data.local.entity.PetSpeciesEntity
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.domain.logic.CareEngine
import com.educalab.huellitasencasa.domain.model.NeedType
import kotlinx.coroutines.flow.Flow

class PetRepository(private val db: HuellitasDatabase) {

    fun observeSpecies(): Flow<List<PetSpeciesEntity>> = db.petSpeciesDao().observeAll()

    suspend fun getSpecies(id: Long): PetSpeciesEntity? = db.petSpeciesDao().getById(id)

    fun observePetsForUser(userProfileId: Long): Flow<List<VirtualPetEntity>> =
        db.virtualPetDao().observeForUser(userProfileId)

    fun observePet(petId: Long): Flow<VirtualPetEntity?> = db.virtualPetDao().observeById(petId)

    suspend fun getPet(petId: Long): VirtualPetEntity? = db.virtualPetDao().getById(petId)

    suspend fun countPetsForUser(userProfileId: Long): Int = db.virtualPetDao().countForUser(userProfileId)

    suspend fun adoptPet(userProfileId: Long, speciesId: Long, name: String, avatarVariant: Int): Long {
        val cleanName = name.trim().ifBlank { "Mi mascota" }.take(16)
        return db.virtualPetDao().insert(
            VirtualPetEntity(
                userProfileId = userProfileId,
                speciesId = speciesId,
                name = cleanName,
                avatarVariant = avatarVariant,
                adoptedAt = System.currentTimeMillis(),
                lastSessionEpochDay = System.currentTimeMillis()
            )
        )
    }

    /**
     * Se llama cada vez que se abre el hogar de una mascota. Si ha pasado suficiente tiempo real
     * (ver [CareEngine.DECAY_INTERVAL_MILLIS]) desde la última visita, aplica un descenso suave
     * (no punitivo) a los seis indicadores, respetando siempre el piso protector de CareEngine.
     * Nota: la columna sigue llamándose "last_session_epoch_day" en la base de datos, pero desde
     * aquí en adelante guarda milisegundos, no un número de día; ambos son solo un Long para SQLite.
     */
    suspend fun applySessionDecayIfNeeded(pet: VirtualPetEntity): VirtualPetEntity {
        val now = System.currentTimeMillis()
        if (!CareEngine.shouldApplyDecay(pet.lastSessionEpochDay, now)) return pet
        val defs = db.petNeedDefinitionDao().getForSpecies(pet.speciesId).associateBy { it.needType }
        val decayed = pet.copy(
            feeding = CareEngine.applySessionDecay(pet.feeding, defs[NeedType.ALIMENTACION.name]?.sessionDecay ?: 8),
            hydration = CareEngine.applySessionDecay(pet.hydration, defs[NeedType.HIDRATACION.name]?.sessionDecay ?: 8),
            hygiene = CareEngine.applySessionDecay(pet.hygiene, defs[NeedType.HIGIENE.name]?.sessionDecay ?: 8),
            activityLevel = CareEngine.applySessionDecay(pet.activityLevel, defs[NeedType.ACTIVIDAD.name]?.sessionDecay ?: 8),
            rest = CareEngine.applySessionDecay(pet.rest, defs[NeedType.DESCANSO.name]?.sessionDecay ?: 8),
            affection = CareEngine.applySessionDecay(pet.affection, defs[NeedType.AFECTO.name]?.sessionDecay ?: 8),
            lastSessionEpochDay = now
        )
        db.virtualPetDao().update(decayed)
        return decayed
    }

    /** Aplica una acción de cuidado real y registra el evento en el historial. */
    suspend fun applyCareAction(pet: VirtualPetEntity, need: NeedType, actionType: String, delta: Int): VirtualPetEntity {
        val updated = when (need) {
            NeedType.ALIMENTACION -> pet.copy(feeding = CareEngine.applyAction(pet.feeding, delta))
            NeedType.HIDRATACION -> pet.copy(hydration = CareEngine.applyAction(pet.hydration, delta))
            NeedType.HIGIENE -> pet.copy(hygiene = CareEngine.applyAction(pet.hygiene, delta))
            NeedType.ACTIVIDAD -> pet.copy(activityLevel = CareEngine.applyAction(pet.activityLevel, delta))
            NeedType.DESCANSO -> pet.copy(rest = CareEngine.applyAction(pet.rest, delta))
            NeedType.AFECTO -> pet.copy(affection = CareEngine.applyAction(pet.affection, delta))
        }
        db.virtualPetDao().update(updated)
        db.careActionDao().insert(
            CareActionEntity(
                virtualPetId = pet.id,
                actionType = actionType,
                needType = need.name,
                delta = delta,
                timestamp = System.currentTimeMillis()
            )
        )
        return updated
    }

    fun indicatorsOf(pet: VirtualPetEntity) = CareEngine.PetIndicators(
        feeding = pet.feeding,
        hydration = pet.hydration,
        hygiene = pet.hygiene,
        activity = pet.activityLevel,
        rest = pet.rest,
        affection = pet.affection
    )
}
