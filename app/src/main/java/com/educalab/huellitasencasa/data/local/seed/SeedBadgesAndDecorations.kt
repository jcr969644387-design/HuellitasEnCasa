package com.educalab.huellitasencasa.data.local.seed

import com.educalab.huellitasencasa.data.local.entity.BadgeEntity
import com.educalab.huellitasencasa.data.local.entity.DecorationEntity

object SeedBadgesAndDecorations {

    val badges = listOf(
        BadgeEntity(code = "PRIMERA_ADOPCION", name = "Primera adopción", description = "Adoptaste a tu primera mascota virtual.", iconRes = "badge_primera_adopcion", tier = 1),
        BadgeEntity(code = "MAESTRO_COMIDAS", name = "Maestro de comidas", description = "Clasificaste correctamente 15 tarjetas de alimentos.", iconRes = "badge_maestro_comidas", tier = 1),
        BadgeEntity(code = "HIGIENE_EXPERTA", name = "Higiene experta", description = "Realizaste 10 acciones de higiene con tu mascota.", iconRes = "badge_higiene_experta", tier = 1),
        BadgeEntity(code = "ENERGIA_ACTIVA", name = "Energía activa", description = "Completaste 10 actividades de juego o paseo.", iconRes = "badge_energia_activa", tier = 1),
        BadgeEntity(code = "GUARDIAN_BIENESTAR", name = "Guardián del bienestar", description = "Respondiste correctamente 10 escenarios de bienestar.", iconRes = "badge_guardian_bienestar", tier = 2),
        BadgeEntity(code = "PLANIFICADOR", name = "Planificador experto", description = "Completaste 5 planes diarios de cuidado.", iconRes = "badge_planificador", tier = 2),
        BadgeEntity(code = "ACADEMIA_ORO", name = "Academia de oro", description = "Terminaste todas las lecciones de la Academia de cuidado.", iconRes = "badge_academia_oro", tier = 2),
        BadgeEntity(code = "AMIGO_FIEL", name = "Amigo fiel", description = "Cuidaste a tu mascota durante 7 sesiones distintas.", iconRes = "badge_amigo_fiel", tier = 2),
        BadgeEntity(code = "VETERINARIO_JUNIOR", name = "Veterinario junior", description = "Respondiste correctamente 20 escenarios de bienestar en total.", iconRes = "badge_veterinario_junior", tier = 3),
        BadgeEntity(code = "DECORADOR", name = "Decorador de hogares", description = "Desbloqueaste 5 decoraciones distintas para el hogar.", iconRes = "badge_decorador", tier = 3)
    )

    val decorations = listOf(
        DecorationEntity(code = "MACETA_FLORES", name = "Maceta con flores", iconRes = "decor_maceta_flores", category = "JARDIN"),
        DecorationEntity(code = "COJIN_ESTRELLA", name = "Cojín con estrella", iconRes = "decor_cojin_estrella", category = "HOGAR"),
        DecorationEntity(code = "LAMPARA_LUNA", name = "Lámpara de luna", iconRes = "decor_lampara_luna", category = "HOGAR"),
        DecorationEntity(code = "ALFOMBRA_RAYAS", name = "Alfombra a rayas", iconRes = "decor_alfombra_rayas", category = "HOGAR"),
        DecorationEntity(code = "CUADRO_PAISAJE", name = "Cuadro de paisaje", iconRes = "decor_cuadro_paisaje", category = "HOGAR"),
        DecorationEntity(code = "MANTA_CUADROS", name = "Manta a cuadros", iconRes = "decor_manta_cuadros", category = "HOGAR"),
        DecorationEntity(code = "ESTANTERIA_LIBROS", name = "Estantería con libros", iconRes = "decor_estanteria_libros", category = "HOGAR"),
        DecorationEntity(code = "FAROLILLO", name = "Farolillo de jardín", iconRes = "decor_farolillo", category = "JARDIN")
    )
}
