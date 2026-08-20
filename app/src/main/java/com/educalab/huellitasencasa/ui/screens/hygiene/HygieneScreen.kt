package com.educalab.huellitasencasa.ui.screens.hygiene

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.model.NeedType
import com.educalab.huellitasencasa.ui.components.IndicatorBar
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.theme.HuellitasSky
import com.educalab.huellitasencasa.util.AppViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private data class HygieneStep(val title: String, val description: String, val delta: Int)

private val steps = listOf(
    HygieneStep("Revisar el pelaje o plumaje", "Comprueba que no haya nudos, suciedad ni heridas visibles.", 6),
    HygieneStep("Cepillar con cuidado", "Un cepillado suave elimina pelo suelto y fortalece el vínculo.", 8),
    HygieneStep("Limpiar su espacio", "Un espacio limpio evita malos olores y enfermedades.", 8)
)

class HygieneViewModel(
    private val petRepo: PetRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {

    private val _pet = MutableStateFlow<VirtualPetEntity?>(null)
    val pet: StateFlow<VirtualPetEntity?> = _pet

    private val _completedSteps = MutableStateFlow<Set<Int>>(emptySet())
    val completedSteps: StateFlow<Set<Int>> = _completedSteps

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback

    fun load(petId: Long) {
        viewModelScope.launch { petRepo.observePet(petId).collect { if (it != null) _pet.value = it } }
    }

    fun completeStep(profileId: Long, index: Int) {
        if (index in _completedSteps.value) return
        val p = _pet.value ?: return
        viewModelScope.launch {
            petRepo.applyCareAction(p, NeedType.HIGIENE, "CEPILLAR", steps[index].delta)
            progressRepo.registerEvent(profileId, "HIGIENE", 1)
            _completedSteps.value = _completedSteps.value + index
            _feedback.value = steps[index].description
        }
    }
}

@Composable
fun HygieneScreen(profileId: Long, petId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: HygieneViewModel = viewModel(factory = AppViewModelFactory(app) { HygieneViewModel(app.petRepository, app.progressRepository) })
    val pet by vm.pet.collectAsState()
    val completed by vm.completedSteps.collectAsState()
    val feedback by vm.feedback.collectAsState()

    LaunchedEffect(petId) { vm.load(petId) }
    val currentPet = pet ?: return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Higiene", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        IndicatorBar("Higiene", currentPet.hygiene, HuellitasSky)
        Spacer(Modifier.height(12.dp))
        Text("Sigue los pasos en orden para un buen aseo:", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        feedback?.let { TipBubble(it, modifier = Modifier.padding(bottom = 10.dp)) }

        steps.forEachIndexed { index, step ->
            val done = index in completed
            val enabled = index == 0 || (index - 1) in completed
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable(enabled = enabled && !done) { vm.completeStep(profileId, index) },
                colors = CardDefaults.cardColors(
                    containerColor = if (done) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (done) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (done) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("${index + 1}. ${step.title}", style = MaterialTheme.typography.titleMedium)
                        if (!enabled) Text("Completa el paso anterior primero", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        if (completed.size == steps.size) {
            Spacer(Modifier.height(12.dp))
            Text("¡Rutina de higiene completa! ${currentPet.name} está reluciente. ✨", style = MaterialTheme.typography.titleMedium)
        }
    }
}
