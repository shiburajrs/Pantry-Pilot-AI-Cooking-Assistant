package com.pantrypilot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pantrypilot.app.ui.navigation.PantryPilotNavHost
import com.pantrypilot.app.ui.theme.PantryPilotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PantryPilotTheme {
                PantryPilotNavHost(
                    onExitApp = { finish() }
                )
            }
        }
    }
}
