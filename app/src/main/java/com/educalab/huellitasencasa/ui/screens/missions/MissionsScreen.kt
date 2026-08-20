package com.educalab.huellitasencasa.ui.screens.missions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.educalab.huellitasencasa.data.local.entity.BadgeEntity
import com.educalab.huellitasencasa.data.local.entity.DecorationEntity
import com.educalab.huellitasencasa.data.local.entity.MissionCompletionEntity
import com.educalab.huellitasencasa.data.local.entity.MissionEntity
import com.educalab.huellitasencasa.data.local.entity.UnlockedDecorationEntity
import com.educalab.huellitasencasa.data.local.entity.UserBadgeEntity
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.logic.MissionEngine
import com.educalab.huellitasencasa.ui.components.BadgeMedallion
import com.educalab.huellitasencasa.util.AppViewModelFactory
import com.educalab.huellitasencasa.util.DrawableCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MissionsViewModel(private val progressRepo: ProgressRepository) : ViewModel() {
    private val _missions = MutableStateFlow<List<MissionEntity>>(emptyList())
    private val _completions = MutableStateFlow<List<MissionCompletionEntity>>(emptyList())
    private val _badges = MutableStateFlow<List<BadgeEntity>>(emptyList())
    private val _userBadges = MutableStateFlow<List<UserBadgeEntity>>(emptyList())
    private val _decorations = MutableStateFlow<List<DecorationEntity>>(emptyList())
    private val _unlockedDecorations = MutableStateFlow<List<UnlockedDecorationEntity>>(emptyList())

    val missions: StateFlow<List<MissionEntity>> = _missions
    val completions: StateFlow<List<MissionCompletionEntity>> = _completions
    val badges: StateFlow<List<BadgeEntity>> = _badges
    val userBadges: StateFlow<List<UserBadgeEntity>> = _userBadges
    val decorations: StateFlow<List<DecorationEntity>> = _decorations
    val unlockedDecorations: StateFlow<List<UnlockedDecorationEntity>> = _unlockedDecorations

    fun load(profileId: Long) {
        viewModelScope.launch { progressRepo.observeMissions().collect { _missions.value = it } }
        viewModelScope.launch { progressRepo.observeCompletions(profileId).collect { _completions.value = it } }
        viewModelScope.launch { progressRepo.observeBadges().collect { _badges.value = it } }
        viewModelScope.launch { progressRepo.observeUserBadges(profileId).collect { _userBadges.value = it } }
        viewModelScope.launch { progressRepo.observeDecorations().collect { _decorations.value = it } }
        viewModelScope.launch { progressRepo.observeUnlockedDecorations(profileId).collect { _unlockedDecorations.value = it } }
    }
}

@Composable
fun MissionsScreen(profileId: Long) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: MissionsViewModel = viewModel(factory = AppViewModelFactory(app) { MissionsViewModel(app.progressRepository) })

    val missions by vm.missions.collectAsState()
    val completions by vm.completions.collectAsState()
    val badges by vm.badges.collectAsState()
    val userBadges by vm.userBadges.collectAsState()
    val decorations by vm.decorations.collectAsState()
    val unlockedDecorations by vm.unlockedDecorations.collectAsState()

    LaunchedEffect(profileId) { vm.load(profileId) }

    var tab by remember { mutableIntStateOf(0) }
    val titles = listOf("Misiones", "Insignias", "Decoraciones")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Misiones, álbum y progreso", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
        TabRow(selectedTabIndex = tab) {
            titles.forEachIndexed { i, title ->
                Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title) })
            }
        }
        Spacer(Modifier.height(10.dp))
        when (tab) {
            0 -> {
                val completionByMission = completions.associateBy { it.missionId }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(missions) { mission ->
                        val completion = completionByMission[mission.id]
                        val progress = completion?.progressCount ?: 0
                        val ratio = MissionEngine.progressRatio(
                            MissionEngine.CompletionState(mission.id, progress, completion?.completed == true),
                            mission.targetCount
                        )
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (completion?.completed == true) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(DrawableCatalog.resolve(mission.iconRes)),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(mission.title, style = MaterialTheme.typography.titleMedium)
                                    Text(mission.description, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(4.dp))
                                    androidx.compose.material3.LinearProgressIndicator(
                                        progress = { ratio },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text("$progress / ${mission.targetCount}", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                val unlockedIds = userBadges.map { it.badgeId }.toSet()
                LazyVerticalGrid(columns = GridCells.Fixed(3), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(badges) { badge ->
                        BadgeMedallion(name = badge.name, iconRes = badge.iconRes, unlocked = badge.id in unlockedIds)
                    }
                }
            }
            2 -> {
                val unlockedIds = unlockedDecorations.map { it.decorationId }.toSet()
                LazyVerticalGrid(columns = GridCells.Fixed(3), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(decorations) { decoration ->
                        BadgeMedallion(name = decoration.name, iconRes = decoration.iconRes, unlocked = decoration.id in unlockedIds)
                    }
                }
            }
        }
    }
}
