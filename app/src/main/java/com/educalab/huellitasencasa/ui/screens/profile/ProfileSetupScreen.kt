package com.educalab.huellitasencasa.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.data.repository.ProfileRepository
import com.educalab.huellitasencasa.ui.components.AppLogo
import com.educalab.huellitasencasa.util.AppViewModelFactory
import kotlinx.coroutines.launch

private val avatarPalette = listOf(
    Color(0xFFFF8A5B), Color(0xFF4FB0A5), Color(0xFFFFC93C), Color(0xFF8E7CC3),
    Color(0xFF5AC8FA), Color(0xFF6FCF97), Color(0xFFF2A6C0), Color(0xFFB5651D)
)

class ProfileSetupViewModel(private val repo: ProfileRepository) : ViewModel() {
    fun createProfile(alias: String, avatarId: Int, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repo.createProfile(alias, avatarId)
            onCreated(id)
        }
    }
}

@Composable
fun ProfileSetupScreen(onProfileCreated: (Long) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: ProfileSetupViewModel = viewModel(factory = AppViewModelFactory(app) { ProfileSetupViewModel(app.profileRepository) })

    var alias by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AppLogo(size = 72.dp)
        }
        androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
        Text("¿Cómo te llamaremos?", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Elige un alias divertido. Nunca pedimos tu nombre real ni ningún otro dato personal.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )
        OutlinedTextField(
            value = alias,
            onValueChange = { if (it.length <= 18) alias = it },
            label = { Text("Tu alias de cuidador/a") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))
        Text("Elige tu avatar", style = MaterialTheme.typography.titleMedium)
        androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(180.dp)
        ) {
            items(avatarPalette.size) { index ->
                val color = avatarPalette[index]
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { selectedAvatar = index },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedAvatar == index) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                        )
                        Text("✓", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
        Button(
            onClick = { vm.createProfile(alias, selectedAvatar, onProfileCreated) },
            modifier = Modifier.fillMaxWidth(),
            enabled = alias.isNotBlank()
        ) {
            Text("Continuar al centro de adopción")
        }
    }
}
