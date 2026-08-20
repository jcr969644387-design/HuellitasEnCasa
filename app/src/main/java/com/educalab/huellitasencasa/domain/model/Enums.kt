package com.educalab.huellitasencasa.domain.model

/**
 * Tipos de necesidad que se cuidan de cada mascota virtual.
 * Todas se mueven en el rango cerrado [0, 100].
 */
enum class NeedType {
    ALIMENTACION,
    HIDRATACION,
    HIGIENE,
    ACTIVIDAD,
    DESCANSO,
    AFECTO
}

/** Especies disponibles en el centro de adopción. */
enum class SpeciesCode {
    PERRO,
    GATO,
    CONEJO,
    HAMSTER,
    AVE
}

/** Categoría de una tarjeta de alimento/situación del minijuego de clasificación. */
enum class FoodCategory {
    ALIMENTO_BUENO,
    ALIMENTO_MALO,
    SITUACION
}

/** Categoría de un objeto del hogar de la mascota. */
enum class HomeItemCategory {
    CUENCO_COMIDA,
    CUENCO_AGUA,
    CAMA,
    JUGUETE,
    HIGIENE,
    ENTORNO
}

/** Franja horaria del planificador de rutinas diarias. */
enum class DaySlot {
    MANANA,
    TARDE,
    NOCHE
}

/** Tipo de acción de cuidado que puede colocarse en un plan diario. */
enum class CareActionType {
    ALIMENTAR,
    DAR_AGUA,
    CEPILLAR,
    LIMPIAR_ESPACIO,
    JUGAR,
    PASEAR,
    DEJAR_DESCANSAR,
    DAR_CARINO
}

/** Tipo/categoría de una misión. */
enum class MissionType {
    ADOPCION,
    ALIMENTACION,
    HIGIENE,
    ACTIVIDAD,
    BIENESTAR,
    PLANIFICADOR,
    ACADEMIA,
    COLECCION
}

/** Categoría visual de una decoración desbloqueable. */
enum class DecorationCategory {
    HOGAR,
    JARDIN,
    JUGUETE
}

/** Estado visual de progreso de un módulo o actividad. */
enum class ProgressState {
    BLOQUEADO,
    DISPONIBLE,
    INICIADO,
    COMPLETADO,
    DOMINADO
}

/** Nivel general de bienestar calculado de una mascota, usado solo para mostrar estado, nunca para castigar. */
enum class WellbeingLevel {
    EXCELENTE,
    BIEN,
    NECESITA_ATENCION,
    ATENCION_URGENTE
}
