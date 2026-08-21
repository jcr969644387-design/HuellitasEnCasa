package com.educalab.huellitasencasa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.educalab.huellitasencasa.domain.model.SpeciesCode

/**
 * Escenario donde vive la mascota mientras se realiza una actividad (hogar, comida, higiene...):
 * un fondo tipo "habitación" con pared y piso, y la mascota apoyada sobre el piso. Se usa en
 * todas las pantallas de cuidado para que la mascota nunca desaparezca de la vista mientras se
 * interactúa con ella; [overlay] permite superponer elementos propios de cada actividad (zonas
 * para soltar objetos, por ejemplo) dentro del mismo escenario.
 */
@Composable
fun PetSceneCard(
    species: SpeciesCode,
    mood: PetMood,
    accent: Color,
    modifier: Modifier = Modifier,
    petSize: Dp = 132.dp,
    sceneHeight: Dp = 220.dp,
    petTopPadding: Dp = 40.dp,
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(sceneHeight)) {
            // Pared
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .background(accent.copy(alpha = 0.14f))
            )
            // Piso
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.46f)
                    .background(accent.copy(alpha = 0.26f))
            )
            // Ventana decorativa
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 18.dp)
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.55f))
            )
            PetIllustration(
                species = species,
                mood = mood,
                size = petSize,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = petTopPadding)
            )
            overlay()
        }
    }
}
