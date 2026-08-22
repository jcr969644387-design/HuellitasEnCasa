package com.educalab.huellitasencasa.ui.screens.hygiene

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.model.NeedType
import com.educalab.huellitasencasa.domain.model.SpeciesCode
import com.educalab.huellitasencasa.ui.components.DraggableCard
import com.educalab.huellitasencasa.ui.components.DropZoneBox
import com.educalab.huellitasencasa.ui.components.DropZoneState
import com.educalab.huellitasencasa.ui.components.IndicatorBar
import com.educalab.huellitasencasa.ui.components.PetMood
import com.educalab.huellitasencasa.ui.components.PetSceneCard
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.components.rememberDropTargetRegistry
import com.educalab.huellitasencasa.ui.theme.HuellitasLeaf
import com.educalab.huellitasencasa.ui.theme.HuellitasSky
import com.educalab.huellitasencasa.util.AppViewModelFactory
import com.educalab.huellitasencasa.util.DrawableCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private data class HygieneStep(val title: String, val description: String, val delta: Int, val iconRes: String)

private val steps = listOf(
    HygieneStep("Revisar el pelaje o plumaje", "Comprueba que no haya nudos, suciedad ni heridas visibles.", 6, "item_lupa"),
    HygieneStep("Cepillar con cuidado", "Un cepillado suave elimina pelo suelto y fortalece el vínculo.", 8, "item_cepillo"),
    HygieneStep("Limpiar su espacio", "Un espacio limpio evita malos olores y enfermedades.", 8, "item_escoba")
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
        viewModelScope.launch {
            val decayed = petRepo.getPet(petId)?.let { petRepo.applySessionDecayIfNeeded(it) }
            _pet.value = decayed
            petRepo.observePet(petId).collect { if (it != null) _pet.value = it }
        }
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

    var speciesCode by remember { mutableStateOf(SpeciesCode.PERRO) }
    LaunchedEffect(currentPet.speciesId) {
        app.petRepository.getSpecies(currentPet.speciesId)?.let {
            speciesCode = runCatching { SpeciesCode.valueOf(it.code) }.getOrDefault(SpeciesCode.PERRO)
        }
    }
    val mood = if (currentPet.hygiene >= 60) PetMood.FELIZ else PetMood.NEUTRAL
    val registry = rememberDropTargetRegistry()
    var hoveringPet by remember { mutableStateOf(false) }
    val currentStepIndex = completed.size
    val allDone = currentStepIndex >= steps.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Higiene", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        PetSceneCard(species = speciesCode, mood = mood, accent = HuellitasSky, modifier = Modifier.padding(bottom = 12.dp)) {
            // La zona donde se puede soltar la herramienta cubre casi toda la escena (no solo
            // un recuadro pequeño sobre la mascota): así el arrastre siempre se registra, sin
            // depender de acertar un punto exacto.
            DropZoneBox(
                id = "PET",
                registry = registry,
                state = if (hoveringPet) DropZoneState.RESALTADA else DropZoneState.VACIA,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {}
            // Un poco de "suciedad" visible durante el paso de limpiar el espacio, para que
            // el usuario tenga algo concreto que barrer con la escoba.
            if (!allDone && currentStepIndex == 2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 14.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8D6E52).copy(alpha = 0.55f))
                        )
                    }
                }
            }
        }
        IndicatorBar("Higiene", currentPet.hygiene, HuellitasSky)
        Spacer(Modifier.height(12.dp))

        // Progreso de los 3 pasos del día (lupa, cepillo, escoba)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
            steps.forEachIndexed { index, step ->
                val done = index in completed
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = if (!done && index != currentStepIndex) Modifier.alpha(0.35f) else Modifier
                ) {
                    Icon(
                        painter = painterResource(DrawableCatalog.resolve(step.iconRes)),
                        contentDescription = step.title,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                    if (done) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Completado",
                            tint = HuellitasLeaf,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        feedback?.let { TipBubble(it, modifier = Modifier.padding(bottom = 10.dp)) }

        if (!allDone) {
            val step = steps[currentStepIndex]
            Text("Paso ${currentStepIndex + 1} de ${steps.size}: ${step.title}", style = MaterialTheme.typography.titleMedium)
            Text(
                "Arrastra la herramienta hasta ${currentPet.name} para completar este paso.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DraggableCard(
                    registry = registry,
                    onDragging = { pos -> hoveringPet = registry.hitTest(pos) == "PET" },
                    onDropped = { target ->
                        hoveringPet = false
                        if (target == "PET") vm.completeStep(profileId, currentStepIndex)
                    }
                ) { dragging ->
                    // Sin fondo opaco: al arrastrar la herramienta sobre la mascota, esta debe
                    // seguir viéndose. Solo un aro sutil indica que se está arrastrando.
                    Box(
                        modifier = Modifier.size(96.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dragging) {
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape)
                                    .background(HuellitasSky.copy(alpha = 0.28f))
                            )
                        }
                        Icon(
                            painter = painterResource(DrawableCatalog.resolve(step.iconRes)),
                            contentDescription = step.title,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        } else {
            Spacer(Modifier.height(4.dp))
            Text("¡Rutina de higiene completa! ${currentPet.name} está reluciente. ✨", style = MaterialTheme.typography.titleMedium)
        }
    }
}
