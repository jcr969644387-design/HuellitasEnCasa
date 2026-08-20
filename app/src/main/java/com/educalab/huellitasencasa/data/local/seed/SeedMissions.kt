package com.educalab.huellitasencasa.data.local.seed

import com.educalab.huellitasencasa.data.local.entity.MissionEntity

/** 30 misiones repartidas en las 8 categorías, con recompensas reales (insignia y/o decoración). */
object SeedMissions {

    private data class Mission(
        val code: String,
        val title: String,
        val description: String,
        val type: String,
        val targetCount: Int,
        val iconRes: String,
        val badgeCode: String?,
        val decorationCode: String?,
        val orderIndex: Int
    )

    private val raw = listOf(
    Mission("M_ADOPTAR_PRIMERA", "Tu primera mascota", "Adopta a tu primera mascota virtual en el centro de adopción.", "ADOPCION", 1, "ic_mod_adopcion", "PRIMERA_ADOPCION", null, 0),
    Mission("M_ADOPTAR_SEGUNDA", "Un nuevo amigo", "Adopta a una segunda mascota virtual.", "ADOPCION", 2, "ic_mod_adopcion", null, "MACETA_FLORES", 1),
    Mission("M_CONOCER_ESPECIES", "Explorador de especies", "Consulta la ficha de 3 especies distintas en el centro de adopción.", "ADOPCION", 3, "ic_mod_adopcion", null, null, 2),
    Mission("M_ALIMENTAR_5", "Buen desayuno", "Alimenta a tu mascota 5 veces.", "ALIMENTACION", 5, "ic_mod_alimentacion", null, null, 3),
    Mission("M_ALIMENTAR_15", "Rutina alimenticia", "Alimenta a tu mascota 15 veces en total.", "ALIMENTACION", 15, "ic_mod_alimentacion", null, "COJIN_ESTRELLA", 4),
    Mission("M_HIDRATAR_10", "Agua fresca siempre", "Da agua fresca a tu mascota 10 veces.", "ALIMENTACION", 10, "ic_mod_alimentacion", null, null, 5),
    Mission("M_CLASIFICAR_15", "Maestro de comidas", "Clasifica correctamente 15 tarjetas de alimentos.", "ALIMENTACION", 15, "ic_mod_alimentacion", "MAESTRO_COMIDAS", null, 6),
    Mission("M_CLASIFICAR_30", "Nutricionista junior", "Clasifica correctamente 30 tarjetas de alimentos.", "ALIMENTACION", 30, "ic_mod_alimentacion", null, "ALFOMBRA_RAYAS", 7),
    Mission("M_HIGIENE_5", "Pelaje brillante", "Realiza 5 acciones de higiene.", "HIGIENE", 5, "ic_mod_higiene", null, null, 8),
    Mission("M_HIGIENE_10", "Higiene experta", "Realiza 10 acciones de higiene con tu mascota.", "HIGIENE", 10, "ic_mod_higiene", "HIGIENE_EXPERTA", null, 9),
    Mission("M_HOGAR_LIMPIO", "Hogar impecable", "Coloca correctamente 5 objetos de higiene o entorno en el hogar.", "HIGIENE", 5, "ic_mod_hogar", null, "LAMPARA_LUNA", 10),
    Mission("M_HIGIENE_20", "Cuidador impecable", "Realiza 20 acciones de higiene en total.", "HIGIENE", 20, "ic_mod_higiene", null, null, 11),
    Mission("M_JUGAR_10", "Energía activa", "Completa 10 actividades de juego o paseo.", "ACTIVIDAD", 10, "ic_mod_actividad", "ENERGIA_ACTIVA", null, 12),
    Mission("M_DESCANSO_5", "Momento de siesta", "Deja descansar a tu mascota 5 veces cuando lo necesite.", "ACTIVIDAD", 5, "ic_mod_actividad", null, null, 13),
    Mission("M_CARINO_10", "Mucho cariño", "Da cariño a tu mascota 10 veces.", "ACTIVIDAD", 10, "ic_mod_actividad", null, "MANTA_CUADROS", 14),
    Mission("M_ACTIVIDAD_25", "Compañero activo", "Suma 25 acciones de actividad, juego o paseo en total.", "ACTIVIDAD", 25, "ic_mod_actividad", null, null, 15),
    Mission("M_BIENESTAR_10", "Observador atento", "Responde correctamente 10 escenarios de bienestar.", "BIENESTAR", 10, "ic_mod_bienestar", "GUARDIAN_BIENESTAR", null, 16),
    Mission("M_BIENESTAR_20", "Veterinario junior", "Responde correctamente 20 escenarios de bienestar en total.", "BIENESTAR", 20, "ic_mod_bienestar", "VETERINARIO_JUNIOR", null, 17),
    Mission("M_BIENESTAR_5", "Primeras señales", "Responde correctamente 5 escenarios de bienestar.", "BIENESTAR", 5, "ic_mod_bienestar", null, null, 18),
    Mission("M_BIENESTAR_30", "Experto en señales", "Responde correctamente los 30 escenarios de bienestar disponibles.", "BIENESTAR", 30, "ic_mod_bienestar", null, "CUADRO_PAISAJE", 19),
    Mission("M_PLAN_1", "Mi primer plan", "Completa tu primer plan diario de cuidado.", "PLANIFICADOR", 1, "ic_mod_planificador", null, null, 20),
    Mission("M_PLAN_5", "Planificador experto", "Completa 5 planes diarios de cuidado.", "PLANIFICADOR", 5, "ic_mod_planificador", "PLANIFICADOR", null, 21),
    Mission("M_PLAN_10", "Rutina maestra", "Completa 10 planes diarios de cuidado en total.", "PLANIFICADOR", 10, "ic_mod_planificador", null, "ESTANTERIA_LIBROS", 22),
    Mission("M_ACADEMIA_3", "Curioso aprendiz", "Completa 3 lecciones de la Academia de cuidado.", "ACADEMIA", 3, "ic_mod_academia", null, null, 23),
    Mission("M_ACADEMIA_TODAS", "Academia de oro", "Completa todas las lecciones de la Academia de cuidado.", "ACADEMIA", 8, "ic_mod_academia", "ACADEMIA_ORO", null, 24),
    Mission("M_ACADEMIA_REPASO", "Repasar y aprender", "Repite 2 lecciones ya completadas para reforzar lo aprendido.", "ACADEMIA", 2, "ic_mod_academia", null, null, 25),
    Mission("M_SESIONES_7", "Amigo fiel", "Cuida a tu mascota durante 7 sesiones distintas.", "COLECCION", 7, "ic_mod_misiones", "AMIGO_FIEL", null, 26),
    Mission("M_DECORAR_5", "Decorador de hogares", "Desbloquea 5 decoraciones distintas para el hogar.", "COLECCION", 5, "ic_mod_misiones", "DECORADOR", null, 27),
    Mission("M_INSIGNIAS_5", "Coleccionista de insignias", "Consigue 5 insignias distintas.", "COLECCION", 5, "ic_mod_misiones", null, "FAROLILLO", 28),
    Mission("M_DECORAR_TODAS", "Hogar completo", "Desbloquea las 8 decoraciones disponibles.", "COLECCION", 8, "ic_mod_misiones", null, null, 29),    )

    fun buildEntities(badgeIdByCode: Map<String, Long>, decorationIdByCode: Map<String, Long>): List<MissionEntity> =
        raw.map { m ->
            MissionEntity(
                code = m.code,
                title = m.title,
                description = m.description,
                type = m.type,
                targetCount = m.targetCount,
                rewardBadgeId = m.badgeCode?.let { badgeIdByCode[it] },
                rewardDecorationId = m.decorationCode?.let { decorationIdByCode[it] },
                orderIndex = m.orderIndex,
                iconRes = m.iconRes
            )
        }

    val size: Int get() = raw.size
}
