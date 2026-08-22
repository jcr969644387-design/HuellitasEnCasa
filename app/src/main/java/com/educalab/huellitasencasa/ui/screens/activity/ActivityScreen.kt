package com.educalab.huellitasencasa.ui.screens.activity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import com.educalab.huellitasencasa.ui.theme.HuellitasCoral
import com.educalab.huellitasencasa.ui.theme.HuellitasYellow
import com.educalab.huellitasencasa.util.AppViewModelFactory
import com.educalab.huellitasencasa.util.DrawableCatalog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Juguete propio de cada especie, con su ícono y el mensaje que celebra el juego. */
private data class SpeciesToy(val iconRes: Int, val prompt: String, val celebration: String)

private fun toyFor(species: SpeciesCode): SpeciesToy = when (species) {
    SpeciesCode.PERRO -> SpeciesToy(R.drawable.item_juguete_pelota, "Arrastra la pelota hasta tu mascota", "¡Le encantó perseguir la pelota!")
    SpeciesCode.GATO -> SpeciesToy(DrawableCatalog.resolve("item_ovillo_lana"), "Arrastra el ovillo de lana hasta tu mascota", "¡Jugó feliz con el ovillo de lana!")
    SpeciesCode.CONEJO -> SpeciesToy(DrawableCatalog.resolve("item_zanahoria"), "Arrastra la zanahoria para que salte hacia ella", "¡Saltó feliz para alcanzar la zanahoria!")
    SpeciesCode.HAMSTER -> SpeciesToy(DrawableCatalog.resolve("item_rueda_ejercicio"), "Arrastra la rueda hasta tu mascota para que corra", "¡Corrió feliz dando vueltas en la rueda!")
    SpeciesCode.AVE -> SpeciesToy(DrawableCatalog.resolve("item_semillas"), "Arrastra las semillas hasta tu mascota para que picotee", "¡Picoteó las semillas con alegría!")
}

/**
 * Las aves no se sacan a pasear: la actividad física que les corresponde es volar. Cada especie
 * recibe la palabra y el mensaje que realmente le corresponden en vez de un "Pasear" genérico.
 */
private data class SpeciesOuting(val label: String, val message: String)

private fun outingFor(species: SpeciesCode): SpeciesOuting = when (species) {
    SpeciesCode.AVE -> SpeciesOuting("Volar", "Un buen vuelo por la jaula, energía renovada.")
    else -> SpeciesOuting("Pasear", "Un buen paseo, energía renovada.")
}

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

    fun play(profileId: Long, celebration: String) = act(profileId, NeedType.ACTIVIDAD, "JUGAR", 16, celebration)
    fun walk(profileId: Long, message: String) = act(profileId, NeedType.ACTIVIDAD, "PASEAR", 14, message)
    fun affection(profileId: Long) = act(profileId, NeedType.AFECTO, "DAR_CARINO", 14, "Se siente muy querido.")

    /** Empieza el descanso: fija el mensaje una vez y da el primer empujón al indicador. */
    fun startResting(profileId: Long) = act(profileId, NeedType.DESCANSO, "DEJAR_DESCANSAR", 5, "Ahora descansa tranquilo mientras duerme.")

    /** Sube el indicador de descanso poco a poco mientras la mascota sigue dormida, sin repetir el mensaje en cada paso. */
    fun restTick(profileId: Long) {
        val p = _pet.value ?: return
        viewModelScope.launch {
            petRepo.applyCareAction(p, NeedType.DESCANSO, "DEJAR_DESCANSAR", 5)
        }
    }

    fun currentRest(): Int = _pet.value?.rest ?: 100

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
    val toy = remember(speciesCode) { toyFor(speciesCode) }
    val outing = remember(speciesCode) { outingFor(speciesCode) }

    // "Descansar" ahora sí duerme a la mascota (ojos cerrados) hasta que se vuelva a tocar el
    // botón, en vez de ser una acción abstracta sin relación visual con el descanso. Mientras
    // duerme, el indicador de descanso sube poco a poco (no de golpe), reflejando que se llena
    // mientras la mascota realmente descansa.
    var resting by remember { mutableStateOf(false) }
    LaunchedEffect(resting) {
        if (resting) {
            vm.startResting(profileId)
            while (vm.currentRest() < 100) {
                delay(900)
                vm.restTick(profileId)
            }
        }
    }
    var caressing by remember { mutableStateOf(false) }
    LaunchedEffect(caressing) {
        if (caressing) {
            delay(1200)
            caressing = false
        }
    }
    val mood = if (resting) PetMood.DORMIDO else PetMood.FELIZ

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

        Text(toy.prompt, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Box(contentAlignment = Alignment.Center) {
                DropZoneBox(id = "PET", registry = registry, state = DropZoneState.VACIA) {
                    PetIllustration(species = speciesCode, mood = mood, size = 120.dp)
                }
                FloatingHearts(
                    visible = caressing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            if (!resting) {
                DraggableCard(registry = registry, onDropped = { target -> if (target == "PET") vm.play(profileId, toy.celebration) }) { _ ->
                    Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(toy.iconRes),
                            contentDescription = "Juguete",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = { vm.walk(profileId, outing.message) }, modifier = Modifier.weight(1f)) { Text(outing.label) }
            OutlinedButton(
                onClick = { resting = !resting },
                modifier = Modifier.weight(1f)
            ) { Text(if (resting) "Despertar" else "Descansar") }
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                caressing = true
                vm.affection(profileId)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Acariciar") }
    }
}

/** Corazones flotantes al acariciar a la mascota, igual que al alimentarla. */
@Composable
private fun FloatingHearts(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) {
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = HuellitasCoral, modifier = Modifier.size(18.dp))
            }
        }
    }
}
