package com.educalab.huellitasencasa.domain.logic

/** Corrige un intento del escenario "¿Qué harías?". Lógica trivial pero centralizada y testeable. */
object ScenarioGrader {
    fun grade(correctOptionIndex: Int, chosenOptionIndex: Int): Boolean =
        correctOptionIndex == chosenOptionIndex
}

/** Resuelve el estado visual (bloqueado/disponible/iniciado/completado/dominado) de un módulo o actividad. */
object ProgressStateResolver {

    enum class State { BLOQUEADO, DISPONIBLE, INICIADO, COMPLETADO, DOMINADO }

    /**
     * @param isUnlocked si el módulo está desbloqueado (por ejemplo, requiere una mascota adoptada).
     * @param attemptsCount cuántas veces se ha interactuado con el módulo.
     * @param correctCount cuántas de esas interacciones fueron correctas/exitosas.
     * @param masteryThreshold a partir de cuántos aciertos se considera "dominado".
     */
    fun resolve(
        isUnlocked: Boolean,
        attemptsCount: Int,
        correctCount: Int,
        masteryThreshold: Int
    ): State = when {
        !isUnlocked -> State.BLOQUEADO
        attemptsCount == 0 -> State.DISPONIBLE
        correctCount >= masteryThreshold -> State.DOMINADO
        attemptsCount > 0 && correctCount > 0 -> State.COMPLETADO
        else -> State.INICIADO
    }
}
