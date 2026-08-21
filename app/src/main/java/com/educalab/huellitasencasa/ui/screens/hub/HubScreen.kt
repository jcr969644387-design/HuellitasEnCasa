package com.educalab.huellitasencasa.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.domain.logic.ProgressStateResolver
import com.educalab.huellitasencasa.domain.logic.WellbeingCalculator
import com.educalab.huellitasencasa.domain.model.SpeciesCode
import com.educalab.huellitasencasa.ui.components.IndicatorBar
import com.educalab.huellitasencasa.ui.components.ModuleCard
import com.educalab.huellitasencasa.ui.components.PetIllustration
import com.educalab.huellitasencasa.ui.components.PetMood
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.theme.HuellitasCoral
import com.educalab.huellitasencasa.ui.theme.HuellitasLavender
import com.educalab.huellitasencasa.ui.theme.HuellitasLeaf
import com.educalab.huellitasencasa.ui.theme.HuellitasOrange
import com.educalab.huellitasencasa.ui.theme.HuellitasSky
import com.educalab.huellitasencasa.ui.theme.HuellitasTeal
import com.educalab.huellitasencasa.ui.theme.HuellitasYellow
import com.educalab.huellitasencasa.util.AppViewModelFactory
import kotlinx.coroutines.launch

class HubViewModel(private val petRepo: PetRepository) : ViewModel() {
    private val _pet = kotlinx.coroutines.flow.MutableStateFlow<VirtualPetEntity?>(null)
    val pet: kotlinx.coroutines.flow.StateFlow<VirtualPetEntity?> = _pet

    fun load(petId: Long) {
        viewModelScope.launch {
            val decayed = petRepo.getPet(petId)?.let { petRepo.applySessionDecayIfNeeded(it) }
            _pet.value = decayed
            petRepo.observePet(petId).collect { _pet.value = it }
        }
    }
}

private data class ModuleDef(val title: String, val subtitle: String, val icon: String, val color: androidx.compose.ui.graphics.Color, val route: String)

private val modules = listOf(
    ModuleDef("Hogar", "Prepara su espacio", "ic_mod_hogar", HuellitasTeal, "home_setup"),
    ModuleDef("Comida", "Alimenta e hidrata", "ic_mod_alimentacion", HuellitasOrange, "feeding"),
    ModuleDef("Higiene", "Aseo y limpieza", "ic_mod_higiene", HuellitasSky, "hygiene"),
    ModuleDef("Actividad", "Juego y descanso", "ic_mod_actividad", HuellitasYellow, "activity"),
    ModuleDef("Bienestar", "¿Qué harías?", "ic_mod_bienestar", HuellitasCoral, "wellbeing"),
    ModuleDef("Planificador", "Rutina del día", "ic_mod_planificador", HuellitasLavender, "planner"),
    ModuleDef("Academia", "Aprende y repasa", "ic_mod_academia", HuellitasLeaf, "academy"),
    ModuleDef("Misiones", "Álbum y progreso", "ic_mod_misiones", HuellitasOrange, "missions")
)

@Composable
fun HubScreen(petId: Long, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: HubViewModel = viewModel(factory = AppViewModelFactory(app) { HubViewModel(app.petRepository) })
    val pet by vm.pet.collectAsState()

    LaunchedEffect(petId) { vm.load(petId) }

    val currentPet = pet ?: return

    val indicators = app.petRepository.indicatorsOf(currentPet)
    val score = WellbeingCalculator.overallScore(indicators)
    val level = WellbeingCalculator.levelFor(score)
    val mood = when {
        score >= 70 -> PetMood.FELIZ
        score >= 45 -> PetMood.NEUTRAL
        indicators.feeding < 40 -> PetMood.HAMBRIENTO
        else -> PetMood.CANSADO
    }

    var speciesCode by remember { mutableStateOf(SpeciesCode.PERRO) }
    LaunchedEffect(currentPet.speciesId) {
        app.petRepository.getSpecies(currentPet.speciesId)?.let {
            speciesCode = runCatching { SpeciesCode.valueOf(it.code) }.getOrDefault(SpeciesCode.PERRO)
        }
    }

    val lowest = WellbeingCalculator.lowestIndicators(indicators).firstOrNull()
    val suggestion = when (lowest) {
        "ALIMENTACION" -> "A ${currentPet.name} le vendría bien comer algo ahora."
        "HIDRATACION" -> "Ofrécele agua fresca a ${currentPet.name}."
        "HIGIENE" -> "Un buen cepillado le sentaría genial a ${currentPet.name}."
        "ACTIVIDAD" -> "${currentPet.name} tiene ganas de jugar."
        "DESCANSO" -> "${currentPet.name} podría aprovechar para descansar."
        else -> "Dale un poco de cariño a ${currentPet.name}."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PetIllustration(species = speciesCode, mood = mood, size = 176.dp)
                Spacer(Modifier.height(4.dp))
                Text(currentPet.name, style = MaterialTheme.typography.headlineMedium)
                Text("Bienestar: ${levelLabel(level)}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(10.dp))
                IndicatorBar("Bienestar general", score, HuellitasTeal, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(10.dp))
        TipBubble(suggestion)
        Spacer(Modifier.height(14.dp))
        Text("Explora Huellitas", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(modules) { m ->
                ModuleCard(
                    title = m.title,
                    subtitle = m.subtitle,
                    iconRes = m.icon,
                    accentColor = m.color,
                    state = ProgressStateResolver.State.DISPONIBLE,
                    onClick = { onNavigate(m.route) }
                )
            }
        }
    }
}

private fun levelLabel(level: com.educalab.huellitasencasa.domain.model.WellbeingLevel): String = when (level) {
    com.educalab.huellitasencasa.domain.model.WellbeingLevel.EXCELENTE -> "Excelente"
    com.educalab.huellitasencasa.domain.model.WellbeingLevel.BIEN -> "Bien"
    com.educalab.huellitasencasa.domain.model.WellbeingLevel.NECESITA_ATENCION -> "Necesita atención"
    com.educalab.huellitasencasa.domain.model.WellbeingLevel.ATENCION_URGENTE -> "Atención urgente"
}
