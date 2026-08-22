package com.educalab.huellitasencasa.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.repository.CareLogRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.logic.PlannerValidator
import com.educalab.huellitasencasa.domain.model.CareActionType
import com.educalab.huellitasencasa.domain.model.DaySlot
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.theme.HuellitasLavender
import com.educalab.huellitasencasa.ui.theme.HuellitasLeaf
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

    private val _selections = MutableStateFlow<Map<DaySlot, CareActionType>>(emptyMap())
    val selections: StateFlow<Map<DaySlot, CareActionType>> = _selections

    private val _completedSlots = MutableStateFlow<Set<DaySlot>>(emptySet())
    val completedSlots: StateFlow<Set<DaySlot>> = _completedSlots

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    fun load(petId: Long) {
        viewModelScope.launch {
            val plan = careLogRepo.getOrCreateTodayPlan(petId)
            careLogRepo.observePlanItems(plan.id).collect { items ->
                if (items.isNotEmpty()) {
                    _saved.value = true
                    _selections.value = items.mapNotNull { item ->
                        val slot = runCatching { DaySlot.valueOf(item.slot) }.getOrNull() ?: return@mapNotNull null
                        val action = runCatching { CareActionType.valueOf(item.careActionType) }.getOrNull() ?: return@mapNotNull null
                        slot to action
                    }.toMap()
                    _completedSlots.value = items
                        .filter { it.isCorrectPlacement }
                        .mapNotNull { runCatching { DaySlot.valueOf(it.slot) }.getOrNull() }
                        .toSet()
                }
            }
        }
    }

    fun select(slot: DaySlot, action: CareActionType) {
        _selections.value = _selections.value + (slot to action)
    }

    fun savePlan(petId: Long, profileId: Long) {
        viewModelScope.launch {
            careLogRepo.savePlanSelections(petId, _selections.value)
            _saved.value = true
            progressRepo.registerEvent(profileId, "PLANIFICADOR", 1)
        }
    }

    fun markDone(petId: Long, slot: DaySlot) {
        viewModelScope.launch {
            careLogRepo.markPlanSlotDone(petId, slot)
            _completedSlots.value = _completedSlots.value + slot
        }
    }

    fun editPlan() {
        _saved.value = false
    }
}

@Composable
fun PlannerScreen(profileId: Long, petId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: PlannerViewModel = viewModel(factory = AppViewModelFactory(app) { PlannerViewModel(app.careLogRepository, app.progressRepository) })
    val selections by vm.selections.collectAsState()
    val completedSlots by vm.completedSlots.collectAsState()
    val saved by vm.saved.collectAsState()

    LaunchedEffect(petId) { vm.load(petId) }

    val slots = listOf(DaySlot.MANANA, DaySlot.TARDE, DaySlot.NOCHE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Planificador de rutinas", style = MaterialTheme.typography.headlineSmall)
        Text(
            if (!saved) "Elige qué harás con tu mascota en cada momento del día."
            else "Este es tu plan de hoy. ¡Marca cada momento cuando lo cumplas!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (!saved) {
            slots.forEach { slot ->
                Text(labelForSlot(slot), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 6.dp, bottom = 6.dp))
                CareActionType.entries.chunked(3).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        row.forEach { action ->
                            val selected = selections[slot] == action
                            Text(
                                labelFor(action),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) HuellitasLavender else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { vm.select(slot, action) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                selections[slot]?.let {
                    Text(
                        PlannerValidator.tipFor(it),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { vm.savePlan(petId, profileId) },
                enabled = selections.keys.containsAll(slots),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar mi plan de hoy")
            }
        } else {
            slots.forEach { slot ->
                val action = selections[slot]
                val done = slot in completedSlots
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (done) HuellitasLeaf.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(labelForSlot(slot), style = MaterialTheme.typography.titleMedium)
                            Text(action?.let { labelFor(it) } ?: "Sin elegir", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (done) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Cumplido", tint = HuellitasLeaf)
                        } else {
                            Button(onClick = { vm.markDone(petId, slot) }, enabled = action != null) {
                                Text("¡Hecho!")
                            }
                        }
                    }
                }
            }
            if (completedSlots.size == slots.size) {
                Spacer(Modifier.height(10.dp))
                TipBubble("¡Cumpliste todo tu plan de hoy! Eso ayuda muchísimo al bienestar de tu mascota.")
            }
            Spacer(Modifier.height(14.dp))
            OutlinedButton(onClick = { vm.editPlan() }, modifier = Modifier.fillMaxWidth()) {
                Text("Cambiar mi plan")
            }
        }
    }
}
