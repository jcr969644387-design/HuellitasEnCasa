package com.educalab.huellitasencasa.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.educalab.huellitasencasa.R
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.model.NeedType
import com.educalab.huellitasencasa.domain.model.SpeciesCode
import com.educalab.huellitasencasa.ui.components.DraggableCard
import com.educalab.huellitasencasa.ui.components.DropZoneBox
import com.educalab.huellitasencasa.ui.components.DropZoneState
import com.educalab.huellitasencasa.ui.components.IndicatorBar
import com.educalab.huellitasencasa.ui.components.PetIllustration
import com.educalab.huellitasencasa.ui.components.PetMood
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.components.rememberDropTargetRegistry
import com.educalab.huellitasencasa.ui.theme.HuellitasYellow
import com.educalab.huellitasencasa.util.AppViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val petRepo: PetRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {
    private val _pet = MutableStateFlow<VirtualPetEntity?>(null)
    val pet: StateFlow<VirtualPetEntity?> = _pet

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback

    fun load(petId: Long) {
        viewModelScope.launch {
            val decayed = petRepo.getPet(petId)?.let { petRepo.applySessionDecayIfNeeded(it) }
            _pet.value = decayed
            petRepo.observePet(petId).collect { if (it != null) _pet.value = it }
        }
    }

    fun play(profileId: Long) = act(profileId, NeedType.ACTIVIDAD, "JUGAR", 16, "¡Le encantó jugar!")
    fun walk(profileId: Long) = act(profileId, NeedType.ACTIVIDAD, "PASEAR", 14, "Un buen paseo, energía renovada.")
    fun rest(profileId: Long) = act(profileId, NeedType.DESCANSO, "DEJAR_DESCANSAR", 18, "Ahora descansa tranquilo.")
    fun affection(profileId: Long) = act(profileId, NeedType.AFECTO, "DAR_CARINO", 14, "Se siente muy querido.")

    private fun act(profileId: Long, need: NeedType, actionType: String, delta: Int, msg: String) {
        val p = _pet.value ?: return
        viewModelScope.launch {
            petRepo.applyCareAction(p, need, actionType, delta)
            progressRepo.registerEvent(profileId, "ACTIVIDAD", 1)
            _feedback.value = msg
        }
    }
}

@Composable
fun ActivityScreen(profileId: Long, petId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: ActivityViewModel = viewModel(factory = AppViewModelFactory(app) { ActivityViewModel(app.petRepository, app.progressRepository) })
    val pet by vm.pet.collectAsState()
    val feedback by vm.feedback.collectAsState()

    LaunchedEffect(petId) { vm.load(petId) }
    val currentPet = pet ?: return

    var speciesCode by remember { mutableStateOf(SpeciesCode.PERRO) }
    LaunchedEffect(currentPet.speciesId) {
        app.petRepository.getSpecies(currentPet.speciesId)?.let {
            speciesCode = runCatching { SpeciesCode.valueOf(it.code) }.getOrDefault(SpeciesCode.PERRO)
        }
    }

    val registry = rememberDropTargetRegistry()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Actividad, juego y descanso", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        IndicatorBar("Actividad", currentPet.activityLevel, HuellitasYellow)
        Spacer(Modifier.height(6.dp))
        IndicatorBar("Descanso", currentPet.rest, MaterialTheme.colorScheme.tertiary)
        Spacer(Modifier.height(6.dp))
        IndicatorBar("Afecto", currentPet.affection, MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))

        feedback?.let { TipBubble(it, modifier = Modifier.padding(bottom = 10.dp)) }

        Text("Arrastra el juguete hasta tu mascota", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            DropZoneBox(id = "PET", registry = registry, state = DropZoneState.VACIA) {
                PetIllustration(species = speciesCode, mood = PetMood.FELIZ, size = 120.dp)
            }
            DraggableCard(registry = registry, onDropped = { target -> if (target == "PET") vm.play(profileId) }) { _ ->
                Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.item_juguete_pelota),
                        contentDescription = "Juguete",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = { vm.walk(profileId) }, modifier = Modifier.weight(1f)) { Text("Pasear") }
            OutlinedButton(onClick = { vm.rest(profileId) }, modifier = Modifier.weight(1f)) { Text("Descansar") }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = { vm.affection(profileId) }, modifier = Modifier.fillMaxWidth()) { Text("Dar cariño") }
    }
}
