package com.educalab.huellitasencasa.ui.screens.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.repository.CareLogRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.logic.PlannerValidator
import com.educalab.huellitasencasa.domain.model.CareActionType
import com.educalab.huellitasencasa.domain.model.DaySlot
import com.educalab.huellitasencasa.ui.components.DraggableCard
import com.educalab.huellitasencasa.ui.components.DropZoneBox
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.components.rememberDropTargetRegistry
import com.educalab.huellitasencasa.util.AppViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private fun labelFor(actionType: CareActionType): String = when (actionType) {
    CareActionType.ALIMENTAR -> "Alimentar"
    CareActionType.DAR_AGUA -> "Dar agua"
    CareActionType.CEPILLAR -> "Cepillar"
    CareActionType.LIMPIAR_ESPACIO -> "Limpiar espacio"
    CareActionType.JUGAR -> "Jugar"
    CareActionType.PASEAR -> "Pasear"
    CareActionType.DEJAR_DESCANSAR -> "Dejar descansar"
    CareActionType.DAR_CARINO -> "Dar cariño"
}

private fun labelForSlot(slot: DaySlot): String = when (slot) {
    DaySlot.MANANA -> "Mañana"
    DaySlot.TARDE -> "Tarde"
    DaySlot.NOCHE -> "Noche"
}

class PlannerViewModel(
    private val careLogRepo: CareLogRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {

    private val _placements = MutableStateFlow<Map<CareActionType, DaySlot>>(emptyMap())
    val placements: StateFlow<Map<CareActionType, DaySlot>> = _placements

    private val _result = MutableStateFlow<PlannerValidator.ValidationResult?>(null)
    val result: StateFlow<PlannerValidator.ValidationResult?> = _result

    fun place(actionType: CareActionType, slot: DaySlot) {
        _placements.value = _placements.value + (actionType to slot)
    }

    fun submit(profileId: Long, petId: Long) {
        viewModelScope.launch {
            val plan = careLogRepo.getOrCreateTodayPlan(petId)
            val items = _placements.value.map { (action, slot) -> PlannerValidator.PlanItem(slot, action) }
            val result = careLogRepo.submitPlan(plan, items)
            _result.value = result
            if (result.isPlanApproved) {
                progressRepo.registerEvent(profileId, "PLANIFICADOR", 1)
            }
        }
    }

    fun reset() {
        _placements.value = emptyMap()
        _result.value = null
    }
}

@Composable
fun PlannerScreen(profileId: Long, petId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: PlannerViewModel = viewModel(factory = AppViewModelFactory(app) { PlannerViewModel(app.careLogRepository, app.progressRepository) })
    val placements by vm.placements.collectAsState()
    val result by vm.result.collectAsState()

    val registry = rememberDropTargetRegistry()
    val pendingActions = CareActionType.entries.filter { it !in placements.keys }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Planificador de rutinas", style = MaterialTheme.typography.headlineSmall)
        Text("Arrastra cada tarjeta a la franja del día donde encaje mejor.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))

        result?.let {
            TipBubble(
                if (it.isPlanApproved) "¡Gran plan! Acertaste ${it.correctCount} de ${it.totalCount} tarjetas."
                else "Acertaste ${it.correctCount} de ${it.totalCount}. Puedes reorganizar y volver a intentarlo.",
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(DaySlot.MANANA, DaySlot.TARDE, DaySlot.NOCHE).forEach { slot ->
                DropZoneBox(id = slot.name, registry = registry, isHighlighted = false, modifier = Modifier.weight(1f).height(140.dp)) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(labelForSlot(slot), style = MaterialTheme.typography.labelLarge)
                        placements.filterValues { it == slot }.keys.forEach { action ->
                            Text("• ${labelFor(action)}", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Text("Tarjetas de cuidado", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            items(pendingActions) { action ->
                DraggableCard(
                    registry = registry,
                    onDropped = { targetId ->
                        val slot = targetId?.let { runCatching { DaySlot.valueOf(it) }.getOrNull() }
                        if (slot != null) vm.place(action, slot)
                    }
                ) { _ ->
                    Card(shape = RoundedCornerShape(14.dp)) {
                        Text(
                            labelFor(action),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { vm.submit(profileId, petId) }, enabled = placements.isNotEmpty(), modifier = Modifier.weight(1f)) {
                Text("Validar plan")
            }
            androidx.compose.material3.OutlinedButton(onClick = { vm.reset() }, modifier = Modifier.weight(1f)) { Text("Reiniciar") }
        }
    }
}
