package com.educalab.huellitasencasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
        setContent {
            HuellitasEnCasaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HuellitasNavGraph()
                }
            }
        }
    }
}
