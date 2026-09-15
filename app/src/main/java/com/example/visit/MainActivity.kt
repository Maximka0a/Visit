package com.example.visit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.visit.presentation.navigationScreen.NavigationViewModel
import com.example.visit.ui.theme.VisitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val navigationViewModel: NavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { navigationViewModel.uiState.value.isLoading }

        enableEdgeToEdge()
        setContent {
            VisitTheme {
                NavigationVisit(navigationViewModel = navigationViewModel)
            }
        }
    }
}