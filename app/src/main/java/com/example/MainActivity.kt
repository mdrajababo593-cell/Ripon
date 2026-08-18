package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CyberpunkParticlesBackground
import com.example.ui.screens.BioCustomizerScreen
import com.example.ui.screens.TokenEngineScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkStoragePermission()
    }
}

@Composable
fun MainAppContent(viewModel: StudioViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Animated Cyberpunk Particle Background
            CyberpunkParticlesBackground()

            // Smooth horizontal screen slide transition
            AnimatedContent(
                targetState = state.currentScreen,
                transitionSpec = {
                    if (targetState == Screen.BIO_CUSTOMIZER) {
                        slideInHorizontally { width -> width } togetherWith
                                slideOutHorizontally { width -> -width }
                    } else {
                        slideInHorizontally { width -> -width } togetherWith
                                slideOutHorizontally { width -> width }
                    }
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.TOKEN_ENGINE -> {
                        TokenEngineScreen(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                    Screen.BIO_CUSTOMIZER -> {
                        BioCustomizerScreen(
                            state = state,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
