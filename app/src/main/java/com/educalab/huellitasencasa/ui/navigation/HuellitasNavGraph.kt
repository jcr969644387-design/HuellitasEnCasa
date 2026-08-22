package com.educalab.huellitasencasa.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.educalab.huellitasencasa.HuellitasApplication
import com.educalab.huellitasencasa.ui.SessionViewModel
import com.educalab.huellitasencasa.ui.screens.academy.AcademyLessonScreen
import com.educalab.huellitasencasa.ui.screens.academy.AcademyListScreen
import com.educalab.huellitasencasa.ui.screens.activity.ActivityScreen
import com.educalab.huellitasencasa.ui.screens.adoption.AdoptionScreen
import com.educalab.huellitasencasa.ui.screens.feeding.FeedingScreen
import com.educalab.huellitasencasa.ui.screens.home.HomeSetupScreen
import com.educalab.huellitasencasa.ui.screens.hub.HubScreen
import com.educalab.huellitasencasa.ui.screens.hygiene.HygieneScreen
import com.educalab.huellitasencasa.ui.screens.missions.MissionsScreen
import com.educalab.huellitasencasa.ui.screens.onboarding.OnboardingScreen
import com.educalab.huellitasencasa.ui.screens.planner.PlannerScreen
import com.educalab.huellitasencasa.ui.screens.profile.ProfileSetupScreen
import com.educalab.huellitasencasa.ui.screens.wellbeing.WellbeingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModuleScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { content() }
    }
}

@Composable
fun HuellitasNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as HuellitasApplication
    val session: SessionViewModel = viewModel()

    var resolvedStart by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val profile = app.database.userProfileDao().getFirstProfile()
        if (profile == null) {
            resolvedStart = Destinations.ONBOARDING
        } else {
            session.setProfile(profile.id)
            val activePet = app.database.virtualPetDao().getActiveForUser(profile.id)
            if (activePet == null) {
                resolvedStart = Destinations.ADOPTION
            } else {
                session.setActivePet(activePet.id)
                val species = app.petRepository.getSpecies(activePet.speciesId)
                val homeItems = species?.let { app.contentRepository.homeItemsFor(it.code) }.orEmpty()
                // Igual que en HomeSetupScreen: el armado inicial solo pide UN objeto por zona,
                // así que la finalización se mide contra ese mismo objeto "principal" por
                // categoría, no contra todas las alternativas de Entorno (que nunca se piden
                // todas y quedarían "sin completar" para siempre).
                val primaryHomeItems = homeItems.groupBy { it.category }.map { (_, group) -> group.first() }
                val completed = app.contentRepository.completedHomeItemIds(activePet.id)
                resolvedStart = if (primaryHomeItems.isNotEmpty() && primaryHomeItems.any { it.id !in completed }) {
                    // El hogar quedó a medio armar (se cerró la app antes de terminar): se retoma.
                    Destinations.HOME_SETUP
                } else {
                    Destinations.HUB
                }
            }
        }
    }

    when (resolvedStart) {
        null -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        else -> {
            NavHost(navController = navController, startDestination = resolvedStart!!) {
                composable(Destinations.ONBOARDING) {
                    OnboardingScreen(onFinished = { navController.navigate(Destinations.PROFILE_SETUP) })
                }
                composable(Destinations.PROFILE_SETUP) {
                    ProfileSetupScreen(onProfileCreated = { profileId ->
                        session.setProfile(profileId)
                        navController.navigate(Destinations.ADOPTION)
                    })
                }
                composable(Destinations.ADOPTION) {
                    val profileId by session.currentProfileId.collectAsState()
                    profileId?.let { pid ->
                        AdoptionScreen(profileId = pid, onAdopted = { petId ->
                            session.setActivePet(petId)
                            // Antes de llegar al centro principal, se arma el hogar por primera vez.
                            navController.navigate(Destinations.HOME_SETUP) {
                                popUpTo(Destinations.ONBOARDING) { inclusive = true }
                            }
                        })
                    }
                }
                composable(Destinations.HUB) {
                    val petId by session.currentPetId.collectAsState()
                    val profileId by session.currentProfileId.collectAsState()
                    petId?.let { pid ->
                        HubScreen(petId = pid, onNavigate = { route -> navController.navigate(route) })
                    }
                }
                composable(Destinations.HOME_SETUP) {
                    val petId by session.currentPetId.collectAsState()
                    val profileId by session.currentProfileId.collectAsState()
                    if (petId != null && profileId != null) {
                        HomeSetupScreen(
                            profileId = profileId!!,
                            petId = petId!!,
                            onCompleted = {
                                navController.navigate(Destinations.HUB) {
                                    popUpTo(Destinations.ONBOARDING) { inclusive = true }
                                }
                            }
                        )
                    }
                }
                composable(Destinations.FEEDING) {
                    val petId by session.currentPetId.collectAsState()
                    val profileId by session.currentProfileId.collectAsState()
                    if (petId != null && profileId != null) {
                        ModuleScaffold("Alimentación y agua", onBack = { navController.popBackStack() }) {
                            FeedingScreen(profileId!!, petId!!)
                        }
                    }
                }
                composable(Destinations.HYGIENE) {
                    val petId by session.currentPetId.collectAsState()
                    val profileId by session.currentProfileId.collectAsState()
                    if (petId != null && profileId != null) {
                        ModuleScaffold("Higiene", onBack = { navController.popBackStack() }) {
                            HygieneScreen(profileId!!, petId!!)
                        }
                    }
                }
                composable(Destinations.ACTIVITY) {
                    val petId by session.currentPetId.collectAsState()
                    val profileId by session.currentProfileId.collectAsState()
                    if (petId != null && profileId != null) {
                        ModuleScaffold("Actividad y descanso", onBack = { navController.popBackStack() }) {
                            ActivityScreen(profileId!!, petId!!)
                        }
                    }
                }
                composable(Destinations.WELLBEING) {
                    val petId by session.currentPetId.collectAsState()
                    val profileId by session.currentProfileId.collectAsState()
                    if (petId != null && profileId != null) {
                        ModuleScaffold("Señales de bienestar", onBack = { navController.popBackStack() }) {
                            WellbeingScreen(profileId!!, petId!!)
                        }
                    }
                }
                composable(Destinations.PLANNER) {
                    val petId by session.currentPetId.collectAsState()
                    val profileId by session.currentProfileId.collectAsState()
                    if (petId != null && profileId != null) {
                        ModuleScaffold("Planificador de rutinas", onBack = { navController.popBackStack() }) {
                            PlannerScreen(profileId!!, petId!!)
                        }
                    }
                }
                composable(Destinations.ACADEMY) {
                    val profileId by session.currentProfileId.collectAsState()
                    profileId?.let { pid ->
                        ModuleScaffold("Academia de cuidado", onBack = { navController.popBackStack() }) {
                            AcademyListScreen(profileId = pid, onOpenLesson = { code -> navController.navigate(Destinations.academyLesson(code)) })
                        }
                    }
                }
                composable(
                    Destinations.ACADEMY_LESSON,
                    arguments = listOf(navArgument("lessonCode") { type = NavType.StringType })
                ) { backStackEntry ->
                    val profileId by session.currentProfileId.collectAsState()
                    val code = backStackEntry.arguments?.getString("lessonCode") ?: return@composable
                    profileId?.let { pid ->
                        ModuleScaffold("Lección", onBack = { navController.popBackStack() }) {
                            AcademyLessonScreen(profileId = pid, lessonCode = code, onBack = { navController.popBackStack() })
                        }
                    }
                }
                composable(Destinations.MISSIONS) {
                    val profileId by session.currentProfileId.collectAsState()
                    profileId?.let { pid ->
                        ModuleScaffold("Misiones y álbum", onBack = { navController.popBackStack() }) {
                            MissionsScreen(profileId = pid)
                        }
                    }
                }
            }
        }
    }
}
