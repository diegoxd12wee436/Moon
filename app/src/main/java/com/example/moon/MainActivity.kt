package com.example.moon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {

    // Usamos 'by viewModels()' para que el ciclo de vida sea el correcto
    private val moonViewModel: MoonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esto permite que la UI use todo el espacio de la pantalla (debajo de la barra de estado)
        enableEdgeToEdge()

        setContent {
            // Surface con el color oscuro de fondo para evitar parpadeos blancos al iniciar
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0B0D1A)
            ) {
                // LLAMADA A LA PANTALLA PRINCIPAL
                MoonFarmScreen(viewModel = moonViewModel)
            }
        }
    }
}