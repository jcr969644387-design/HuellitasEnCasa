package com.educalab.huellitasencasa.domain.logic

/**
 * Reglas puras de progreso de misiones. El progreso siempre se deriva de una acción real
 * ocurrida en la app (una alimentación, una respuesta correcta, un plan completado...),
 * nunca de un contador simulado.
 */
object MissionEngine {

    data class MissionDef(val id: Long, val type: String, val targetCount: Int)
    data class CompletionState(val missionId: Long, val progressCount: Int, val completed: Boolean)

    /**
     * Aplica un evento de progreso (por ejemplo, "ALIMENTACION" +1) a todas las misiones de ese
     * tipo que el cuidador aún no ha completado. Devuelve el nuevo estado de cada misión afectada.
     */
    fun applyEvent(
        missions: List<MissionDef>,
        currentStates: Map<Long, CompletionState>,
        eventType: String,
        amount: Int = 1
    ): List<CompletionState> {
        require(amount > 0) { "amount debe ser positivo" }
        return missions
            .filter { it.type == eventType }
            .map { mission ->
                val current = currentStates[mission.id] ?: CompletionState(mission.id, 0, false)
                if (current.completed) {
                    current
                } else {
                    val newProgress = (current.progressCount + amount).coerceAtMost(mission.targetCount)
                    CompletionState(
                        missionId = mission.id,
                        progressCount = newProgress,
                        completed = newProgress >= mission.targetCount
                    )
                }
            }
    }

    fun progressRatio(state: CompletionState, target: Int): Float =
        if (target <= 0) 0f else (state.progressCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
}
