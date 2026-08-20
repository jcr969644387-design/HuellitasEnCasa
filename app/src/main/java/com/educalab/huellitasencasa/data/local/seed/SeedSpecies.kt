package com.educalab.huellitasencasa.data.local.seed

import com.educalab.huellitasencasa.data.local.entity.PetNeedDefinitionEntity
import com.educalab.huellitasencasa.data.local.entity.PetSpeciesEntity
import com.educalab.huellitasencasa.domain.model.NeedType

/**
 * Contenido semilla de especies. Los IDs se asignan por Room al insertar (autoGenerate),
 * así que el resto del seed hace referencia a las especies por su ORDEN de inserción,
 * ver [SeedProvider] para el mapeo real id->code una vez insertadas.
 */
object SeedSpecies {

    val speciesList = listOf(
        PetSpeciesEntity(
            code = "PERRO",
            displayName = "Perro",
            description = "Compañero leal y juguetón. Necesita paseos, ejercicio y mucha compañía.",
            iconRes = "ic_mod_adopcion"
        ),
        PetSpeciesEntity(
            code = "GATO",
            displayName = "Gato",
            description = "Independiente y curioso. Necesita un espacio propio, arañador y rutina tranquila.",
            iconRes = "ic_mod_adopcion"
        ),
        PetSpeciesEntity(
            code = "CONEJO",
            displayName = "Conejo",
            description = "Herbívoro sensible. Necesita heno fresco siempre disponible y un espacio seguro.",
            iconRes = "ic_mod_adopcion"
        ),
        PetSpeciesEntity(
            code = "HAMSTER",
            displayName = "Hámster",
            description = "Pequeño roedor nocturno. Necesita una jaula enriquecida y silencio de día.",
            iconRes = "ic_mod_adopcion"
        ),
        PetSpeciesEntity(
            code = "AVE",
            displayName = "Ave doméstica",
            description = "Inteligente y sociable. Necesita estímulos, vuelo controlado y una dieta variada.",
            iconRes = "ic_mod_adopcion"
        )
    )

    /**
     * Consejo educativo y velocidad de descenso por sesión para cada necesidad y especie.
     * sessionDecay es el máximo que puede bajar un indicador al iniciar una nueva sesión de
     * juego (nunca por temporizadores en segundo plano, ver CareEngine.applySessionDecay).
     */
    fun needDefinitionsFor(speciesId: Long, speciesCode: String): List<PetNeedDefinitionEntity> {
        val tips: Map<String, Map<NeedType, String>> = mapOf(
            "PERRO" to mapOf(
                NeedType.ALIMENTACION to "Los perros comen 1-2 veces al día; evita darles chocolate o huesos de pollo.",
                NeedType.HIDRATACION to "El agua fresca debe estar siempre disponible, sobre todo tras jugar o pasear.",
                NeedType.HIGIENE to "Cepilla su pelaje 2-3 veces por semana para evitar nudos y caída excesiva.",
                NeedType.ACTIVIDAD to "Necesita paseos diarios y juegos activos para gastar energía.",
                NeedType.DESCANSO to "Duerme muchas horas; respeta su cama y sus siestas.",
                NeedType.AFECTO to "Le encanta el contacto social: caricias, juego y atención regular."
            ),
            "GATO" to mapOf(
                NeedType.ALIMENTACION to "Prefiere comidas pequeñas y frecuentes; nunca le des cebolla ni ajo.",
                NeedType.HIDRATACION to "Muchos gatos beben poco: coloca el agua lejos de la comida para animarlo.",
                NeedType.HIGIENE to "Se asea solo, pero cepillarlo ayuda a reducir bolas de pelo.",
                NeedType.ACTIVIDAD to "Necesita juego corto e intenso con estímulos que simulen caza.",
                NeedType.DESCANSO to "Duerme gran parte del día; necesita un lugar tranquilo y alto.",
                NeedType.AFECTO to "Aprecia el cariño en sus propios términos, sin forzar el contacto."
            ),
            "CONEJO" to mapOf(
                NeedType.ALIMENTACION to "El heno debe ser el 80% de su dieta, disponible todo el día.",
                NeedType.HIDRATACION to "Necesita agua limpia constante, revisada a diario.",
                NeedType.HIGIENE to "Su espacio debe limpiarse con frecuencia para evitar humedad.",
                NeedType.ACTIVIDAD to "Necesita espacio para saltar y explorar cada día.",
                NeedType.DESCANSO to "Es más activo al amanecer y al atardecer; respeta su ritmo.",
                NeedType.AFECTO to "Se gana su confianza poco a poco, con calma y paciencia."
            ),
            "HAMSTER" to mapOf(
                NeedType.ALIMENTACION to "Mezcla de semillas y algo de verdura fresca en pequeñas cantidades.",
                NeedType.HIDRATACION to "Usa un bebedero limpio y revisa que no gotee ni se atasque.",
                NeedType.HIGIENE to "Cambia el sustrato de su jaula regularmente para que esté seco.",
                NeedType.ACTIVIDAD to "La rueda de ejercicio es esencial para su bienestar nocturno.",
                NeedType.DESCANSO to "Es nocturno: evita despertarlo durante el día.",
                NeedType.AFECTO to "Se acostumbra a las manos con manipulación breve y suave."
            ),
            "AVE" to mapOf(
                NeedType.ALIMENTACION to "Combina semillas, fruta y verdura fresca; evita el aguacate.",
                NeedType.HIDRATACION to "El agua del bebedero debe cambiarse cada día.",
                NeedType.HIGIENE to "Ofrécele un baño poco profundo para que se limpie las plumas.",
                NeedType.ACTIVIDAD to "Necesita tiempo fuera de la jaula en un espacio seguro para volar.",
                NeedType.DESCANSO to "Necesita entre 10 y 12 horas de oscuridad y silencio para dormir.",
                NeedType.AFECTO to "Responde bien a la voz calmada y a la interacción diaria breve."
            )
        )
        val speciesTips = tips[speciesCode] ?: emptyMap()
        return NeedType.entries.map { need ->
            PetNeedDefinitionEntity(
                speciesId = speciesId,
                needType = need.name,
                sessionDecay = 8,
                careTip = speciesTips[need] ?: "Cuida a tu mascota con atención regular."
            )
        }
    }
}
