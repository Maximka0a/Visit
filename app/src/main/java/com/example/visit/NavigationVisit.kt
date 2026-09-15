package com.example.visit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.visit.presentation.ProfileScreen.EditProfileRoute
import com.example.visit.presentation.ProfileScreen.EditProfileScreen
import com.example.visit.presentation.contactsScreen.ContactsScreen
import com.example.visit.presentation.contactsScreen.RouteContactsScreen
import com.example.visit.presentation.myCardScreen.MyCardRoute
import com.example.visit.presentation.navitationScreen.NavigationViewModel
import com.example.visit.presentation.scannerScreen.ScannerScreen
import kotlinx.serialization.Serializable

sealed interface ProfileScreen {
    @Serializable
    data object MyProfile : ProfileScreen
    @Serializable
    data object EditProfile : ProfileScreen
    @Serializable
    data object Scanner : ProfileScreen
    @Serializable
    data object Contacts:  ProfileScreen
}

@Composable
fun NavigationVisit(
    navigationViewModel: NavigationViewModel = hiltViewModel()
){
    val navUiState by navigationViewModel.uiState.collectAsStateWithLifecycle()

    if (navUiState.isLoading) {
        // простой загрузочный экран, например Box с CircularProgressIndicator по центру
        return
    }
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hasRoute<ProfileScreen.EditProfile>() != true

    Scaffold(
        bottomBar = {
            if (showBottomBar){
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Face, contentDescription = "Моя визитка") },
                        label = { Text("Моя визитка") },
                        selected = currentDestination?.hasRoute<ProfileScreen.MyProfile>() == true,
                        onClick = { navController.navigate(ProfileScreen.MyProfile) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Сканировать") },
                        label = { Text("Сканировать") },
                        selected = currentDestination?.hasRoute<ProfileScreen.Scanner>() == true,
                        onClick = { navController.navigate(ProfileScreen.Scanner) }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Знакомства") },
                        label = { Text("Знакомства") },
                        selected = currentDestination?.hasRoute<ProfileScreen.Contacts>() == true,
                        onClick = { navController.navigate(ProfileScreen.Contacts) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (navUiState.hasProfile) ProfileScreen.MyProfile else ProfileScreen.EditProfile,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<ProfileScreen.EditProfile>{
                EditProfileRoute(
                    onNavigateToCard = {navController.navigate(ProfileScreen.MyProfile)}
                )
            }
            composable<ProfileScreen.MyProfile>{
                MyCardRoute(
                    onNavitateToEdit = {navController.navigate(ProfileScreen.EditProfile)}
                )
            }
            composable<ProfileScreen.Scanner>{
                ScannerScreen()
            }
            composable<ProfileScreen.Contacts>{
                RouteContactsScreen()
            }
        }
    }
}