package edu.ucne.registroocupaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.registroocupaciones.presentation.ocupacion.OcupacionMainScreen
import edu.ucne.registroocupaciones.ui.theme.RegistroOcupacionesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistroOcupacionesTheme {
                OcupacionMainScreen()
            }
        }
    }
}