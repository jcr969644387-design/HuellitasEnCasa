package com.educalab.huellitasencasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.educalab.huellitasencasa.ui.navigation.HuellitasNavGraph
import com.educalab.huellitasencasa.ui.theme.HuellitasEnCasaTheme

/**
 * Única Activity de la aplicación. Toda la navegación entre módulos ocurre dentro de
 * Jetpack Compose Navigation (ver [HuellitasNavGraph]); no hay pantallas nativas adicionales.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()
        setContent {
            HuellitasEnCasaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HuellitasNavGraph()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    /**
     * Modo inmersivo: la barra de navegación y la barra de estado quedan ocultas mientras se
     * juega (como en la mayoría de juegos móviles) y reaparecen temporalmente con un swipe desde
     * el borde. Esto evita ademas cualquier choque visual con el notch o la isla dinamica, ya que
     * no se dibuja contenido de sistema sobre la pantalla.
     */
    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
