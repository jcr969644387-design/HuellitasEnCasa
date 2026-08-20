package com.educalab.huellitasencasa.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.educalab.huellitasencasa.R
import com.educalab.huellitasencasa.ui.theme.HuellitasOrange
import com.educalab.huellitasencasa.ui.theme.HuellitasOrangeLight
import kotlinx.coroutines.launch

private data class OnboardingPage(val title: String, val body: String, val iconRes: Int)

private val pages = listOf(
    OnboardingPage(
        "Bienvenido a Huellitas En Casa",
        "Aquí vas a adoptar y cuidar mascotas virtuales: perros, gatos, conejos, hámsters y aves. Cada una tiene sus propias necesidades.",
        R.drawable.logo_huellitas
    ),
    OnboardingPage(
        "Cuida, aprende y juega",
        "Alimenta, asea, entretén y observa a tu mascota. Cada acción real que hagas la ayuda de verdad: aquí nadie castiga por no jugar un día.",
        R.drawable.ic_onboarding_care
    ),
    OnboardingPage(
        "Gana insignias y decora",
        "Completa misiones para desbloquear insignias y objetos decorativos para el hogar de tu mascota.",
        R.drawable.badge_amigo_fiel
    ),
    OnboardingPage(
        "Todo queda guardado en tu dispositivo",
        "Huellitas En Casa funciona sin conexión a internet. No pedimos tu nombre real, ni email, ni ubicación: solo un alias que tú elijas.",
        R.drawable.ic_onboarding_privacy
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HuellitasOrangeLight)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 28.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { index ->
            val page = pages[index]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(page.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp),
                        tint = Color.Unspecified
                    )
                }
                Spacer(Modifier.height(28.dp))
                Text(page.title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(page.body, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pages.indices.forEach { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == pagerState.currentPage) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (i == pagerState.currentPage) HuellitasOrange else HuellitasOrange.copy(alpha = 0.3f))
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (!isLastPage) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinished()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (!isLastPage) "Siguiente" else "Empezar a cuidar")
        }
        if (!isLastPage) {
            TextButton(onClick = onFinished) { Text("Saltar") }
        }
    }
}
