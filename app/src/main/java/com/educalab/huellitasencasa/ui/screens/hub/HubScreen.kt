package com.educalab.huellitasencasa.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.local.entity.HomeItemEntity
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.repository.ContentRepository
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.domain.logic.ProgressStateResolver
import com.educalab.huellitasencasa.domain.logic.WellbeingCalculator
import com.educalab.huellitasencasa.domain.model.SpeciesCode
import com.educalab.huellitasencasa.ui.components.IndicatorBar
import com.educalab.huellitasencasa.ui.components.ModuleCard
import com.educalab.huellitasencasa.ui.components.PetMood
import com.educalab.huellitasencasa.ui.components.PetSceneCard
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.theme.HuellitasCoral
import com.educalab.huellitasencasa.ui.theme.HuellitasLavender
import com.educalab.huellitasencasa.ui.theme.HuellitasLeaf
import com.educalab.huellitasencasa.ui.theme.HuellitasTeal
import com.educalab.huellitasencasa.ui.theme.HuellitasYellow
import com.educalab.huellitasencasa.util.AppViewModelFactory
import com.educalab.huellitasencasa.util.DrawableCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HubViewModel(
    private val petRepo: PetRepository,
    private val contentRepo: ContentRepository
) : ViewModel() {
    private val _pet = MutableStateFlow<VirtualPetEntity?>(null)
    val pet: StateFlow<VirtualPetEntity?> = _pet

    private val _homeItems = MutableStateFlow<List<HomeItemEntity>>(emptyList())
    val homeItems: StateFlow<List<HomeItemEntity>> = _homeItems

    private val _placedIds = MutableStateFlow<Set<Long>>(emptySet())
    val placedIds: StateFlow<Set<Long>> = _placedIds

    fun load(petId: Long) {
        viewModelScope.launch {
            val decayed = petRepo.getPet(petId)?.let { petRepo.applySessionDecayIfNeeded(it) }
            _pet.value = decayed
            val species = decayed?.let { petRepo.getSpecies(it.speciesId) }
            _homeItems.value = species?.let { contentRepo.homeItemsFor(it.code) }.orEmpty()
            _placedIds.value = contentRepo.completedHomeItemIds(petId)
            petRepo.observePet(petId).collect { _pet.value = it }
        }
    }
}

// Solo quedan estos 4 en la grilla: los demás módulos ahora se abren tocando el objeto
// correspondiente alrededor de la mascota (ver mapeo en handleItemTap).
private data class ModuleDef(val title: String, val subtitle: String, val icon: String, val color: androidx.compose.ui.graphics.Color, val route: String)

private val modules = listOf(
    ModuleDef("Bienestar", "¿Qué harías?", "ic_mod_bienestar", HuellitasCoral, "wellbeing"),
    ModuleDef("Planificador", "Rutina del día", "ic_mod_planificador", HuellitasLavender, "planner"),
    ModuleDef("Academia", "Aprende y repasa", "ic_mod_academia", HuellitasLeaf, "academy"),
    ModuleDef("Misiones", "Álbum y progreso", "ic_mod_misiones", HuellitasYellow, "missions")
)

private fun routeForCategory(category: String): String? = when (category) {
    "CUENCO_COMIDA", "CUENCO_AGUA" -> "feeding"
    "JUGUETE" -> "activity"
    "HIGIENE" -> "hygiene"
    else -> null
}

@Composable
fun HubScreen(petId: Long, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: HubViewModel = viewModel(factory = AppViewModelFactory(app) { HubViewModel(app.petRepository, app.contentRepository) })
    val pet by vm.pet.collectAsState()
    val homeItems by vm.homeItems.collectAsState()
    val placedIds by vm.placedIds.collectAsState()

    LaunchedEffect(petId) { vm.load(petId) }

    val currentPet = pet ?: return

    val indicators = app.petRepository.indicatorsOf(currentPet)
    val score = WellbeingCalculator.overallScore(indicators)
    val level = WellbeingCalculator.levelFor(score)
    val baseMood = when {
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

    // Interacciones locales de la escena: dormir (visual, no navega) y elegir el objeto de
    // entorno que se muestra (no persiste entre reinicios, pero no requiere tocar el esquema).
    // Dormir es un interruptor manual: la mascota no despierta sola, hay que volver a tocar
    // la cama para despertarla.
    var sleeping by remember { mutableStateOf(false) }
    val effectiveMood = if (sleeping) PetMood.DORMIDO else baseMood

    val placedItems = homeItems.filter { it.id in placedIds }
    val entornoItems = placedItems.filter { it.category == "ENTORNO" }
    var featuredEntornoId by remember(entornoItems) { mutableStateOf(entornoItems.firstOrNull()?.id) }
    var showEntornoPicker by remember { mutableStateOf(false) }

    val sceneItems = listOfNotNull(
        placedItems.firstOrNull { it.category == "CUENCO_COMIDA" },
        placedItems.firstOrNull { it.category == "CUENCO_AGUA" },
        placedItems.firstOrNull { it.category == "CAMA" },
        placedItems.firstOrNull { it.category == "JUGUETE" },
        placedItems.firstOrNull { it.category == "HIGIENE" },
        entornoItems.firstOrNull { it.id == featuredEntornoId }
    )
    val leftItems = sceneItems.take(3)
    val rightItems = sceneItems.drop(3)

    fun handleItemTap(item: HomeItemEntity) {
        when (item.category) {
            "CAMA" -> sleeping = !sleeping
            "ENTORNO" -> showEntornoPicker = true
            else -> routeForCategory(item.category)?.let { onNavigate(it) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(currentPet.name, style = MaterialTheme.typography.headlineMedium)
                Text("Bienestar: ${levelLabel(level)}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                PetSceneCard(
                    species = speciesCode,
                    mood = effectiveMood,
                    accent = HuellitasTeal,
                    petSize = 140.dp,
                    sceneHeight = 340.dp,
                    petTopPadding = 30.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        leftItems.forEach { item ->
                            HubHomeItemChip(
                                item = item,
                                active = item.category == "CAMA" && sleeping,
                                onClick = { handleItemTap(item) }
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rightItems.forEach { item ->
                            HubHomeItemChip(
                                item = item,
                                active = item.category == "CAMA" && sleeping,
                                onClick = { handleItemTap(item) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                IndicatorBar("Bienestar general", score, HuellitasTeal, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                Spacer(Modifier.height(4.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        TipBubble(suggestion)
        Spacer(Modifier.height(8.dp))
        Text(
            "Toca cada objeto junto a ${currentPet.name}: comida y agua abren Alimentación, el " +
                "cepillo abre Higiene, el juguete abre Actividad, la cama la duerme (toca de nuevo " +
                "para despertarla) y el entorno se puede cambiar.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        Text("Explora Huellitas", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(modules) { m ->
                ModuleCard(
                    title = m.title,
                    subtitle = m.subtitle,
                    iconRes = m.icon,
                    accentColor = m.color,
                    state = ProgressStateResolver.State.DISPONIBLE,
                    onClick = { onNavigate(m.route) },
                    compact = true
                )
            }
        }
    }

    if (showEntornoPicker) {
        AlertDialog(
            onDismissRequest = { showEntornoPicker = false },
            confirmButton = { TextButton(onClick = { showEntornoPicker = false }) { Text("Listo") } },
            title = { Text("Entorno de ${currentPet.name}") },
            text = {
                Column {
                    Text(
                        "Elige qué objeto de entorno mostrar junto a tu mascota.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    entornoItems.forEach { item ->
                        val selected = item.id == featuredEntornoId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) HuellitasTeal.copy(alpha = 0.16f) else Color.Transparent)
                                .clickable { featuredEntornoId = item.id }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(DrawableCatalog.resolve(item.iconRes)),
                                contentDescription = item.name,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(item.name, style = MaterialTheme.typography.titleSmall)
                                Text(item.description, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun HubHomeItemChip(item: HomeItemEntity, active: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.88f))
            .then(
                if (active) {
                    Modifier.border(2.dp, HuellitasTeal, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            painter = painterResource(DrawableCatalog.resolve(item.iconRes)),
            contentDescription = item.name,
            tint = Color.Unspecified,
            modifier = Modifier.size(38.dp)
        )
    }
}

private fun levelLabel(level: com.educalab.huellitasencasa.domain.model.WellbeingLevel): String = when (level) {
    com.educalab.huellitasencasa.domain.model.WellbeingLevel.EXCELENTE -> "Excelente"
    com.educalab.huellitasencasa.domain.model.WellbeingLevel.BIEN -> "Bien"
    com.educalab.huellitasencasa.domain.model.WellbeingLevel.NECESITA_ATENCION -> "Necesita atención"
    com.educalab.huellitasencasa.domain.model.WellbeingLevel.ATENCION_URGENTE -> "Atención urgente"
}
