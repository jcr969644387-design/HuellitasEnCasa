package com.educalab.huellitasencasa.domain.logic

import com.educalab.huellitasencasa.domain.model.WellbeingLevel

/**
 * Calcula el bienestar general de la mascota a partir de sus seis indicadores.
 * Se usa únicamente para mostrar estado y desbloquear contenido educativo,
 * nunca para "castigar" ni para bloquear el juego.
 */
object WellbeingCalculator {

    /** Media simple de los seis indicadores, redondeada al entero más cercano. */
    fun overallScore(indicators: CareEngine.PetIndicators): Int {
        val sum = indicators.feeding + indicators.hydration + indicators.hygiene +
            indicators.activity + indicators.rest + indicators.affection
        return Math.round(sum / 6.0).toInt().coerceIn(0, 100)
    }

    fun levelFor(score: Int): WellbeingLevel = when {
        score >= 80 -> WellbeingLevel.EXCELENTE
        score >= 55 -> WellbeingLevel.BIEN
        score >= 30 -> WellbeingLevel.NECESITA_ATENCION
        else -> WellbeingLevel.ATENCION_URGENTE
    }

    /** Indicador(es) con el valor más bajo: útil para sugerir la siguiente actividad en el Home. */
    fun lowestIndicators(indicators: CareEngine.PetIndicators): List<String> {
        val map = linkedMapOf(
            "ALIMENTACION" to indicators.feeding,
            "HIDRATACION" to indicators.hydration,
            "HIGIENE" to indicators.hygiene,
            "ACTIVIDAD" to indicators.activity,
            "DESCANSO" to indicators.rest,
            "AFECTO" to indicators.affection
        )
        val min = map.values.min()
        return map.filterValues { it == min }.keys.toList()
    }
}
