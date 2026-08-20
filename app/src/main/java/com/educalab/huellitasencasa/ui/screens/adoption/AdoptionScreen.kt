package com.educalab.huellitasencasa.ui.screens.adoption

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.local.entity.PetSpeciesEntity
import com.educalab.huellitasencasa.data.repository.PetRepository
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.model.SpeciesCode
import com.educalab.huellitasencasa.ui.components.PetIllustration
import com.educalab.huellitasencasa.ui.components.PetMood
import com.educalab.huellitasencasa.util.AppViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdoptionViewModel(
    private val petRepo: PetRepository,
    private val progressRepo: ProgressRepository
) : ViewModel() {

    private val _species = MutableStateFlow<List<PetSpeciesEntity>>(emptyList())
    val species: StateFlow<List<PetSpeciesEntity>> = _species

    init {
        viewModelScope.launch {
            petRepo.observeSpecies().collect { _species.value = it }
        }
    }

    fun adopt(userProfileId: Long, speciesId: Long, name: String, onAdopted: (Long) -> Unit) {
        viewModelScope.launch {
            val petId = petRepo.adoptPet(userProfileId, speciesId, name, avatarVariant = 0)
            progressRepo.registerEvent(userProfileId, "ADOPCION", 1)
            onAdopted(petId)
        }
    }
}

@Composable
fun AdoptionScreen(profileId: Long, onAdopted: (Long) -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: AdoptionViewModel = viewModel(factory = AppViewModelFactory(app) { AdoptionViewModel(app.petRepository, app.progressRepository) })
    val speciesList by vm.species.collectAsState()

    var selected by remember { mutableStateOf<PetSpeciesEntity?>(null) }
    var petName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Centro de adopción", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Elige a tu nuevo compañero. Cada especie tiene necesidades distintas.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(speciesList) { sp ->
                val speciesCode = runCatching { SpeciesCode.valueOf(sp.code) }.getOrDefault(SpeciesCode.PERRO)
                Card(
                    onClick = { selected = sp },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected?.id == sp.id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PetIllustration(species = speciesCode, mood = PetMood.FELIZ, size = 76.dp)
                        Column {
                            Text(sp.displayName, style = MaterialTheme.typography.titleMedium)
                            Text(sp.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        if (selected != null) {
            OutlinedTextField(
                value = petName,
                onValueChange = { if (it.length <= 16) petName = it },
                label = { Text("Ponle un nombre a tu ${selected!!.displayName.lowercase()}") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { vm.adopt(profileId, selected!!.id, petName, onAdopted) },
                enabled = petName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adoptar a ${petName.ifBlank { "..." }}")
            }
        }
    }
}
