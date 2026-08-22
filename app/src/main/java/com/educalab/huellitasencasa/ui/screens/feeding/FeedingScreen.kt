package com.educalab.huellitasencasa.ui.screens.feeding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.local.entity.FoodItemEntity
import com.educalab.huellitasencasa.data.local.entity.VirtualPetEntity
import com.educalab.huellitasencasa.data.repository.ContentRepository
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.model.NeedType
import com.educalab.huellitasencasa.domain.model.SpeciesCode
import com.educalab.huellitasencasa.ui.components.IndicatorBar
import com.educalab.huellitasencasa.ui.components.PetMood
import com.educalab.huellitasencasa.ui.components.PetSceneCard
import com.educalab.huellitasencasa.ui.components.TipBubble
import com.educalab.huellitasencasa.ui.theme.HuellitasCoral
import com.educalab.huellitasencasa.ui.theme.HuellitasOrange
import com.educalab.huellitasencasa.ui.theme.HuellitasSky
import com.educalab.huellitasencasa.util.AppViewModelFactory
import com.educalab.huellitasencasa.util.DrawableCatalog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedingViewModel(
    private val petRepo: PetRepository,
    private val contentRepo: ContentRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {

    private val _pet = MutableStateFlow<VirtualPetEntity?>(null)
    val pet: StateFlow<VirtualPetEntity?> = _pet

    private val _cards = MutableStateFlow<List<FoodItemEntity>>(emptyList())
    val cards: StateFlow<List<FoodItemEntity>> = _cards

    private val _cardIndex = MutableStateFlow(0)
    val cardIndex: StateFlow<Int> = _cardIndex

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback

    private val _preferredFoods = MutableStateFlow<List<FoodItemEntity>>(emptyList())
    val preferredFoods: StateFlow<List<FoodItemEntity>> = _preferredFoods

    fun load(petId: Long, profileId: Long) {
        viewModelScope.launch {
            val decayed = petRepo.getPet(petId)?.let { petRepo.applySessionDecayIfNeeded(it) }
            _pet.value = decayed
            petRepo.observePet(petId).collect { p -> if (p != null) _pet.value = p }
        }
        viewModelScope.launch {
            val p = petRepo.getPet(petId) ?: return@launch
            _cards.value = contentRepo.randomFoodCards(p.speciesId, profileId, 10)
            _preferredFoods.value = contentRepo.preferredFoodsFor(p.speciesId, 4)
        }
    }

    fun feed(profileId: Long) = act(profileId, NeedType.ALIMENTACION, "ALIMENTAR", +18)
    fun water(profileId: Long) = act(profileId, NeedType.HIDRATACION, "DAR_AGUA", +18)

    private fun act(profileId: Long, need: NeedType, actionType: String, delta: Int) {
        val p = _pet.value ?: return
        viewModelScope.launch {
            petRepo.applyCareAction(p, need, actionType, delta)
            progressRepo.registerEvent(profileId, "ALIMENTACION", 1)
        }
    }

    fun classify(profileId: Long, item: FoodItemEntity, userSaysAppropriate: Boolean) {
        viewModelScope.launch {
            val p = _pet.value
            val correct = contentRepo.recordFoodAttempt(profileId, item, p?.id, userSaysAppropriate)
            _feedback.value = if (correct) "¡Bien! ${item.explanation}" else "No exactamente. ${item.explanation}"
            if (correct) progressRepo.registerEvent(profileId, "ALIMENTACION", 1)
            _cardIndex.value = (_cardIndex.value + 1)
        }
    }
}

@Composable
fun FeedingScreen(profileId: Long, petId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: FeedingViewModel = viewModel(factory = AppViewModelFactory(app) {
        FeedingViewModel(app.petRepository, app.contentRepository, app.progressRepository)
    })
    val pet by vm.pet.collectAsState()
    val cards by vm.cards.collectAsState()
    val index by vm.cardIndex.collectAsState()
    val feedback by vm.feedback.collectAsState()
    val preferredFoods by vm.preferredFoods.collectAsState()

    LaunchedEffect(petId) { vm.load(petId, profileId) }
    val currentPet = pet ?: return

    var speciesCode by remember { mutableStateOf(SpeciesCode.PERRO) }
    LaunchedEffect(currentPet.speciesId) {
        app.petRepository.getSpecies(currentPet.speciesId)?.let {
            speciesCode = runCatching { SpeciesCode.valueOf(it.code) }.getOrDefault(SpeciesCode.PERRO)
        }
    }
    val baseMood = when {
        currentPet.feeding < 40 || currentPet.hydration < 40 -> PetMood.HAMBRIENTO
        currentPet.feeding >= 70 && currentPet.hydration >= 70 -> PetMood.FELIZ
        else -> PetMood.NEUTRAL
    }
    var celebrating by remember { mutableStateOf(false) }
    LaunchedEffect(celebrating) {
        if (celebrating) {
            delay(1400)
            celebrating = false
        }
    }
    val effectiveMood = if (celebrating) PetMood.FELIZ else baseMood
    var selectedFoodId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Alimentación y agua", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        PetSceneCard(species = speciesCode, mood = effectiveMood, accent = HuellitasOrange, modifier = Modifier.padding(bottom = 12.dp)) {
            FloatingHearts(
                visible = celebrating,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
            )
        }
        IndicatorBar("Alimentación", currentPet.feeding, HuellitasOrange)
        Spacer(Modifier.height(6.dp))
        IndicatorBar("Hidratación", currentPet.hydration, HuellitasSky)
        Spacer(Modifier.height(14.dp))

        if (preferredFoods.isNotEmpty()) {
            Text("A ${currentPet.name} le encanta:", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                preferredFoods.forEach { food ->
                    val selected = selectedFoodId == food.id
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) HuellitasOrange.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) HuellitasOrange else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedFoodId = food.id }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(DrawableCatalog.resolve(food.iconRes)),
                            contentDescription = food.name,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(food.name, style = MaterialTheme.typography.labelSmall, maxLines = 1, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { celebrating = true; vm.feed(profileId) }, modifier = Modifier.weight(1f)) { Text("Alimentar") }
            OutlinedButton(onClick = { celebrating = true; vm.water(profileId) }, modifier = Modifier.weight(1f)) {
                Icon(
                    painter = painterResource(DrawableCatalog.resolve("item_cuenco_agua")),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Dar agua")
            }
        }
        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(12.dp))
        Text("Clasifica y Cuida", style = MaterialTheme.typography.titleLarge)
        Text("¿Es apto para tu mascota?", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(10.dp))

        feedback?.let { TipBubble(it, modifier = Modifier.padding(bottom = 10.dp)) }

        if (index < cards.size) {
            val item = cards[index]
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(DrawableCatalog.resolve(item.iconRes)),
                        contentDescription = item.name,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { vm.classify(profileId, item, true) }, modifier = Modifier.weight(1f)) { Text("✔ Es adecuado") }
                OutlinedButton(onClick = { vm.classify(profileId, item, false) }, modifier = Modifier.weight(1f)) { Text("✘ No es adecuado") }
            }
            Text("Tarjeta ${index + 1} de ${cards.size}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        } else if (cards.isNotEmpty()) {
            Text("¡Completaste esta ronda de clasificación! Vuelve más tarde para practicar de nuevo.", style = MaterialTheme.typography.bodyLarge)
        } else {
            Text("¡Ya completaste esta ronda de clasificación! Vuelve en unas horas para practicar con nuevas tarjetas.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * Corazones flotantes que aparecen brevemente al alimentar o dar agua. Se define como
 * composable propio (en vez de un [AnimatedVisibility] inline dentro de otro Column) para que
 * la resolución de sobrecargas no confunda la variante de [AnimatedVisibility] con la extensión
 * de ColumnScope del mismo nombre.
 */
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
                Icon(Icons.Filled.Favorite, contentDescription = null, tint = HuellitasCoral, modifier = Modifier.size(20.dp))
            }
        }
    }
}
