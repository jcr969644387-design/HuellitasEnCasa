package com.educalab.huellitasencasa.domain.logic

/**
 * Reglas puras de cuidado de la mascota. No dependen de Android ni de Room: se pueden
 * testear en JVM puro. Ningún indicador puede salir de [0, 100] y, además, nunca se permite
 * que una ausencia del jugador haga bajar un indicador por debajo de [PROTECTED_FLOOR]:
 * HuellitasEnCasa nunca "castiga" ni deja "morir" a la mascota por no jugar.
 */
object CareEngine {

    const val MIN_VALUE = 0
    const val MAX_VALUE = 100
    /** Piso protector: la mascota nunca aparece en un estado crítico solo por el paso del tiempo. */
    const val PROTECTED_FLOOR = 15
    /** Cada cuánto tiempo real puede volver a aplicarse el descenso suave (no punitivo). */
    const val DECAY_INTERVAL_MILLIS = 2L * 60L * 60L * 1000L

    fun clamp(value: Int): Int = value.coerceIn(MIN_VALUE, MAX_VALUE)

    /**
     * Aplica una acción de cuidado real (alimentar, jugar, etc.) a un indicador.
     * [delta] puede ser positivo (mejora) o, en casos educativos muy puntuales, negativo.
     */
    fun applyAction(currentValue: Int, delta: Int): Int = clamp(currentValue + delta)

    /**
     * Aplica el suave descenso de una nueva sesión de juego. Nunca se llama por temporizadores
     * en segundo plano: solo se invoca al detectar que ha pasado al menos [DECAY_INTERVAL_MILLIS]
     * desde la última sesión registrada. El descenso está limitado a [maxDecay] sin importar
     * cuánto tiempo haya pasado realmente, y jamás baja del piso protector.
     */
    fun applySessionDecay(currentValue: Int, maxDecay: Int): Int {
        val decayed = currentValue - maxDecay.coerceAtLeast(0)
        return decayed.coerceIn(PROTECTED_FLOOR, MAX_VALUE)
    }

    /**
     * true si ha pasado suficiente tiempo real desde la última sesión registrada como para
     * aplicar un nuevo descenso. Antes se comparaba por día de calendario, lo que hacía que los
     * indicadores nunca se movieran dentro de una misma sesión de juego real.
     */
    fun shouldApplyDecay(lastSessionAtMillis: Long, nowMillis: Long): Boolean =
        nowMillis - lastSessionAtMillis >= DECAY_INTERVAL_MILLIS

    data class PetIndicators(
        val feeding: Int,
        val hydration: Int,
        val hygiene: Int,
        val activity: Int,
        val rest: Int,
        val affection: Int
    ) {
        init {
            require(listOf(feeding, hydration, hygiene, activity, rest, affection).all { it in MIN_VALUE..MAX_VALUE }) {
                "Todos los indicadores deben estar en [0,100]"
            }
        }
    }

    /** Aplica el descenso de sesión a los seis indicadores de una mascota a la vez. */
    fun applySessionDecayToAll(indicators: PetIndicators, maxDecay: Int): PetIndicators = PetIndicators(
        feeding = applySessionDecay(indicators.feeding, maxDecay),
        hydration = applySessionDecay(indicators.hydration, maxDecay),
        hygiene = applySessionDecay(indicators.hygiene, maxDecay),
        activity = applySessionDecay(indicators.activity, maxDecay),
        rest = applySessionDecay(indicators.rest, maxDecay),
        affection = applySessionDecay(indicators.affection, maxDecay)
    )
}
