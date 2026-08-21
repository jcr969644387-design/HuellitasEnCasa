package com.educalab.huellitasencasa.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.logic.WellbeingCalculator
import com.educalab.huellitasencasa.domain.model.SpeciesCode
import com.educalab.huellitasencasa.ui.components.DraggableCard
import com.educalab.huellitasencasa.ui.components.DropZoneBox
import com.educalab.huellitasencasa.ui.components.DropZoneState
import com.educalab.huellitasencasa.ui.components.PetMood
import com.educalab.huellitasencasa.ui.components.PetSceneCard
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.components.rememberDropTargetRegistry
import com.educalab.huellitasencasa.ui.theme.HuellitasLeaf
import com.educalab.huellitasencasa.ui.theme.HuellitasTeal
import com.educalab.huellitasencasa.util.AppViewModelFactory
import com.educalab.huellitasencasa.util.DrawableCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val zoneLabels = mapOf(
    "CUENCO_COMIDA" to "Zona de comida",
    "CUENCO_AGUA" to "Zona de agua",
    "CAMA" to "Zona de descanso",
    "JUGUETE" to "Zona de juego",
    "HIGIENE" to "Zona de aseo",
    "ENTORNO" to "Zona de entorno"
)

class HomeSetupViewModel(
    private val petRepo: PetRepository,
    private val contentRepo: ContentRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {

    private val _pet = MutableStateFlow<VirtualPetEntity?>(null)
    val pet: StateFlow<VirtualPetEntity?> = _pet

    private val _items = MutableStateFlow<List<HomeItemEntity>>(emptyList())
    val items: StateFlow<List<HomeItemEntity>> = _items

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback

    private val _placedIds = MutableStateFlow<Set<Long>>(emptySet())
    val placedIds: StateFlow<Set<Long>> = _placedIds

    fun load(petId: Long) {
        viewModelScope.launch {
            val p = petRepo.getPet(petId) ?: return@launch
            _pet.value = p
            val species = petRepo.getSpecies(p.speciesId)
            _items.value = species?.let { contentRepo.homeItemsFor(it.code) }.orEmpty()
        }
    }

    fun handleDrop(profileId: Long, item: HomeItemEntity, zoneId: String?) {
        val p = _pet.value ?: return
        viewModelScope.launch {
            if (zoneId == null) return@launch
            val correct = zoneId == item.category
            contentRepo.recordHomePlacement(p.id, item, correct)
            if (correct) {
                _placedIds.value = _placedIds.value + item.id
                progressRepo.registerEvent(profileId, "HIGIENE", 1)
                _feedback.value = "¡Correcto! ${item.description}"
            } else {
                _feedback.value = "Ese objeto encaja mejor en otra zona. ${item.description}"
            }
        }
    }

    fun clearFeedback() { _feedback.value = null }
}

@Composable
fun HomeSetupScreen(profileId: Long, petId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: HomeSetupViewModel = viewModel(factory = AppViewModelFactory(app) {
        HomeSetupViewModel(app.petRepository, app.contentRepository, app.progressRepository)
    })
    val pet by vm.pet.collectAsState()
    val items by vm.items.collectAsState()
    val feedback by vm.feedback.collectAsState()
    val placedIds by vm.placedIds.collectAsState()

    LaunchedEffect(petId) { vm.load(petId) }
    val currentPet = pet

    var speciesCode by remember { mutableStateOf(SpeciesCode.PERRO) }
    LaunchedEffect(currentPet?.speciesId) {
        currentPet?.let { p ->
            app.petRepository.getSpecies(p.speciesId)?.let {
                speciesCode = runCatching { SpeciesCode.valueOf(it.code) }.getOrDefault(SpeciesCode.PERRO)
            }
        }
    }
    val mood = currentPet?.let { p ->
        val score = WellbeingCalculator.overallScore(app.petRepository.indicatorsOf(p))
        when {
            score >= 70 -> PetMood.FELIZ
            score >= 45 -> PetMood.NEUTRAL
            else -> PetMood.CANSADO
        }
    } ?: PetMood.NEUTRAL

    val registry = rememberDropTargetRegistry()
    val pendingItems = items.filter { it.id !in placedIds }
    val placedItems = items.filter { it.id in placedIds }
    var hoveredZoneId by remember { mutableStateOf<String?>(null) }
    val zoneRows = zoneLabels.entries.toList().chunked(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Prepara el hogar", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Arrastra cada objeto hasta la zona donde crees que pertenece.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        feedback?.let {
            TipBubble(it, modifier = Modifier.padding(bottom = 8.dp))
        }

        PetSceneCard(
            species = speciesCode,
            mood = mood,
            accent = HuellitasTeal,
            petSize = 108.dp,
            sceneHeight = 400.dp,
            petTopPadding = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Objetos ya colocados, a tamaño real, justo delante de la mascota
                if (placedItems.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        placedItems.forEach { p ->
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(DrawableCatalog.resolve(p.iconRes)),
                                    contentDescription = p.name,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }
                    }
                }
                zoneRows.forEach { rowZones ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        rowZones.forEach { (zoneId, label) ->
                            val placedHere = items.any { it.category == zoneId && it.id in placedIds }
                            val zoneState = when {
                                hoveredZoneId == zoneId -> DropZoneState.RESALTADA
                                placedHere -> DropZoneState.LLENA
                                else -> DropZoneState.VACIA
                            }
                            DropZoneBox(
                                id = zoneId,
                                registry = registry,
                                state = zoneState,
                                modifier = Modifier.weight(1f).height(64.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (placedHere) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Zona completa",
                                            tint = HuellitasLeaf,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text("Objetos disponibles", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            items(pendingItems, key = { it.id }) { item ->
                DraggableCard(
                    registry = registry,
                    onDragging = { pos -> hoveredZoneId = registry.hitTest(pos) },
                    onDropped = { zoneId ->
                        hoveredZoneId = null
                        vm.handleDrop(profileId, item, zoneId)
                    }
                ) { dragging ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (dragging) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (dragging) 8.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp).size(84.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(DrawableCatalog.resolve(item.iconRes)),
                                contentDescription = item.name,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(item.name, style = MaterialTheme.typography.labelMedium, maxLines = 2)
                        }
                    }
                }
            }
        }
        if (pendingItems.isEmpty() && items.isNotEmpty()) {
            Text("¡Hogar completo! Has colocado todos los objetos.", style = MaterialTheme.typography.titleMedium)
        }
    }
}
