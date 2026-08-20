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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.educalab.huellitasencasa.data.local.entity.HomeItemEntity
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.repository.ContentRepository
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.ui.components.DraggableCard
import com.educalab.huellitasencasa.ui.components.DropZoneBox
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.components.rememberDropTargetRegistry
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
    val items by vm.items.collectAsState()
    val feedback by vm.feedback.collectAsState()
    val placedIds by vm.placedIds.collectAsState()

    androidx.compose.runtime.LaunchedEffect(petId) { vm.load(petId) }

    val registry = rememberDropTargetRegistry()
    val pendingItems = items.filter { it.id !in placedIds }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Prepara el hogar", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Arrastra cada objeto hasta la zona donde crees que pertenece.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        feedback?.let {
            TipBubble(it, modifier = Modifier.padding(bottom = 8.dp))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(zoneLabels.entries.toList()) { (zoneId, label) ->
                DropZoneBox(id = zoneId, registry = registry, isHighlighted = false, modifier = Modifier.fillMaxWidth().height(96.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(label, style = MaterialTheme.typography.labelLarge)
                        val placedHere = items.filter { it.category == zoneId && it.id in placedIds }
                        Row {
                            placedHere.forEach { p ->
                                Icon(
                                    painter = painterResource(DrawableCatalog.resolve(p.iconRes)),
                                    contentDescription = p.name,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Objetos disponibles", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            items(pendingItems) { item ->
                DraggableCard(
                    registry = registry,
                    onDropped = { zoneId -> vm.handleDrop(profileId, item, zoneId) }
                ) { dragging ->
                    Card(shape = RoundedCornerShape(16.dp)) {
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
