package com.educalab.huellitasencasa.ui.screens.academy

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.educalab.huellitasencasa.data.local.entity.AcademyLessonProgressEntity
import com.educalab.huellitasencasa.data.repository.ProgressRepository
import com.educalab.huellitasencasa.domain.model.AcademyCurriculum
import com.educalab.huellitasencasa.domain.model.AcademyLesson
import com.educalab.huellitasencasa.ui.components.ProgressStateChip
import com.educalab.huellitasencasa.domain.logic.ProgressStateResolver
import com.educalab.huellitasencasa.util.AppViewModelFactory
import com.educalab.huellitasencasa.util.DrawableCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AcademyViewModel(private val progressRepo: ProgressRepository) : ViewModel() {
    private val _progress = MutableStateFlow<List<AcademyLessonProgressEntity>>(emptyList())
    val progress: StateFlow<List<AcademyLessonProgressEntity>> = _progress

    fun load(profileId: Long) {
        viewModelScope.launch { progressRepo.observeAcademyProgress(profileId).collect { _progress.value = it } }
    }

    fun markViewed(profileId: Long, lessonCode: String) {
        viewModelScope.launch { progressRepo.markLessonViewed(profileId, lessonCode) }
    }
}

@Composable
fun AcademyListScreen(profileId: Long, onOpenLesson: (String) -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: AcademyViewModel = viewModel(factory = AppViewModelFactory(app) { AcademyViewModel(app.progressRepository) })
    val progress by vm.progress.collectAsState()
    LaunchedEffect(profileId) { vm.load(profileId) }

    val completedCodes = progress.filter { it.completed }.map { it.lessonCode }.toSet()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Academia de cuidado", style = MaterialTheme.typography.headlineSmall)
        Text("Lecciones breves para aprender a cuidar mejor.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(AcademyCurriculum.lessons) { lesson ->
                val done = lesson.code in completedCodes
                Card(
                    onClick = { onOpenLesson(lesson.code) },
                    colors = CardDefaults.cardColors(containerColor = if (done) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(DrawableCatalog.resolve(lesson.iconRes)),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                            Text(lesson.summary, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.width(8.dp))
                        ProgressStateChip(if (done) ProgressStateResolver.State.COMPLETADO else ProgressStateResolver.State.DISPONIBLE)
                    }
                }
            }
        }
    }
}

@Composable
fun AcademyLessonScreen(profileId: Long, lessonCode: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val vm: AcademyViewModel = viewModel(factory = AppViewModelFactory(app) { AcademyViewModel(app.progressRepository) })
    val lesson: AcademyLesson = AcademyCurriculum.lessons.first { it.code == lessonCode }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Icon(
            painter = painterResource(DrawableCatalog.resolve(lesson.iconRes)),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(lesson.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        lesson.bodyParagraphs.forEach { paragraph ->
            Text(paragraph, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 10.dp))
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { vm.markViewed(profileId, lesson.code); onBack() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Entendido, marcar como completada") }
    }
}
