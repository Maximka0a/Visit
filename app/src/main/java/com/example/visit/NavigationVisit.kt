package com.example.visit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.visit.presentation.contactsScreen.RouteContactsScreen
import com.example.visit.presentation.editProfileScreen.EditProfileRoute
import com.example.visit.presentation.myCardScreen.MyCardRoute
import com.example.visit.presentation.navigationScreen.NavigationViewModel
import com.example.visit.presentation.scannerScreen.ScannerScreen
import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object MyProfile : AppRoute
    @Serializable
    data object EditProfile : AppRoute
    @Serializable
    data object Scanner : AppRoute
    @Serializable
    data object Contacts:  AppRoute
}

@Composable
fun NavigationVisit(
    navigationViewModel: NavigationViewModel = hiltViewModel()
){
    val navUiState by navigationViewModel.uiState.collectAsStateWithLifecycle()

    if (navUiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hasRoute<AppRoute.EditProfile>() != true

    Scaffold(
        bottomBar = {
            if (showBottomBar){
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Face, contentDescription = "Моя визитка") },
                        label = { Text("Моя визитка") },
                        selected = currentDestination?.hasRoute<AppRoute.MyProfile>() == true,
                        onClick = { navController.navigate(AppRoute.MyProfile) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Сканировать") },
                        label = { Text("Сканировать") },
                        selected = currentDestination?.hasRoute<AppRoute.Scanner>() == true,
                        onClick = { navController.navigate(AppRoute.Scanner) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Знакомства") },
                        label = { Text("Знакомства") },
                        selected = currentDestination?.hasRoute<AppRoute.Contacts>() == true,
                        onClick = { navController.navigate(AppRoute.Contacts) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (navUiState.hasProfile) AppRoute.MyProfile else AppRoute.EditProfile,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<AppRoute.EditProfile>{
                EditProfileRoute(
                    onNavigateToCard = {navController.navigate(AppRoute.MyProfile)}
                )
            }
            composable<AppRoute.MyProfile>{
                MyCardRoute(
                    onNavigateToEdit = {navController.navigate(AppRoute.EditProfile)}
                )
            }
            composable<AppRoute.Scanner>{
                ScannerScreen()
            }
            composable<AppRoute.Contacts>{
                RouteContactsScreen()
            }
        }
    }
}
