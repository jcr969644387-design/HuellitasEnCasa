package com.educalab.huellitasencasa.ui.screens.wellbeing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.local.entity.WellbeingScenarioEntity
import com.educalab.huellitasencasa.data.repository.ContentRepository
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.util.AppViewModelFactory
import com.educalab.huellitasencasa.util.DrawableCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WellbeingViewModel(
    private val petRepo: PetRepository,
    private val contentRepo: ContentRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {

    private val _pet = MutableStateFlow<VirtualPetEntity?>(null)
    val pet: StateFlow<VirtualPetEntity?> = _pet

    private val _scenarios = MutableStateFlow<List<WellbeingScenarioEntity>>(emptyList())
    val scenarios: StateFlow<List<WellbeingScenarioEntity>> = _scenarios

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index

    private val _feedback = MutableStateFlow<Pair<Boolean, String>?>(null)
    val feedback: StateFlow<Pair<Boolean, String>?> = _feedback

    fun load(petId: Long, profileId: Long) {
        viewModelScope.launch {
            val p = petRepo.getPet(petId) ?: return@launch
            _pet.value = p
            _scenarios.value = contentRepo.randomScenarios(p.speciesId, profileId, 6)
        }
    }

    fun answer(profileId: Long, scenario: WellbeingScenarioEntity, chosenIndex: Int) {
        viewModelScope.launch {
            val correct = contentRepo.recordScenarioAttempt(profileId, scenario, _pet.value?.id, chosenIndex)
            val extra = if (scenario.recommendAskAdult) " Recuerda: ante señales así, siempre es buena idea avisar a un adulto." else ""
            _feedback.value = correct to (scenario.explanation + extra)
            if (correct) progressRepo.registerEvent(profileId, "BIENESTAR", 1)
        }
    }

    fun next() {
        _feedback.value = null
        _index.value += 1
    }
}

@Composable
fun WellbeingScreen(profileId: Long, petId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: WellbeingViewModel = viewModel(factory = AppViewModelFactory(app) {
        WellbeingViewModel(app.petRepository, app.contentRepository, app.progressRepository)
    })
    val scenarios by vm.scenarios.collectAsState()
    val index by vm.index.collectAsState()
    val feedback by vm.feedback.collectAsState()

    LaunchedEffect(petId) { vm.load(petId, profileId) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Señales de bienestar", style = MaterialTheme.typography.headlineSmall)
        Text("¿Qué harías en esta situación?", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 12.dp))

        if (index < scenarios.size) {
            val scenario = scenarios[index]
            val options = scenario.optionsCsv.split("|")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(DrawableCatalog.resolve(scenario.iconRes)),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(scenario.situationText, style = MaterialTheme.typography.titleMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            Spacer(Modifier.height(14.dp))

            if (feedback == null) {
                options.forEachIndexed { i, option ->
                    OutlinedButton(
                        onClick = { vm.answer(profileId, scenario, i) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) { Text(option) }
                }
            } else {
                val (correct, explanation) = feedback!!
                TipBubble(if (correct) "¡Correcto! $explanation" else "Casi. $explanation")
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { vm.next() }, modifier = Modifier.fillMaxWidth()) { Text("Siguiente situación") }
            }
            Text("Situación ${index + 1} de ${scenarios.size}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 10.dp))
        } else if (scenarios.isNotEmpty()) {
            Text("¡Completaste esta ronda de escenarios! Vuelve más tarde para ver situaciones nuevas.", style = MaterialTheme.typography.bodyLarge)
        } else {
            Text("¡Ya completaste esta ronda de escenarios! Vuelve en unas horas para ver situaciones nuevas.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
