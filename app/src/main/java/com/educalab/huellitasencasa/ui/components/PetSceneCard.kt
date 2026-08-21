package com.educalab.huellitasencasa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
            // Pared, zocalo y piso, sin superposicion entre ellos (evita cualquier "costura" rara)
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .background(accent.copy(alpha = 0.14f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(accent.copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .background(accent.copy(alpha = 0.26f))
                )
            }
            // Ventana decorativa: cielo con marco en cruz, para que se lea como ventana y no
            // como un rectángulo suelto.
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFBEE3F8))
                    .border(2.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.White.copy(alpha = 0.85f))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.85f))
                )
            }
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
