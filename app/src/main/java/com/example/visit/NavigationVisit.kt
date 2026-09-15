package com.example.visit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.visit.presentation.contactDetailScreen.ContactDetailRoute
import com.example.visit.presentation.contactsScreen.RouteContactsScreen
import com.example.visit.presentation.editProfileScreen.EditProfileRoute
import com.example.visit.presentation.myCardScreen.MyCardRoute
import com.example.visit.presentation.navigationScreen.NavigationViewModel
import com.example.visit.presentation.scannerScreen.ScannerScreen
import com.example.visit.ui.theme.Spacing
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
    @Serializable
    data class ContactDetail(val id: Long) : AppRoute
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

    val showBottomBar = currentDestination?.hasRoute<AppRoute.EditProfile>() != true &&
        currentDestination?.hasRoute<AppRoute.ContactDetail>() != true

    Scaffold(
        bottomBar = {
            if (showBottomBar){
                VisitBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { route -> navController.navigate(route) }
                )
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
                RouteContactsScreen(
                    onContactClick = { id -> navController.navigate(AppRoute.ContactDetail(id)) }
                )
            }
            composable<AppRoute.ContactDetail>{
                ContactDetailRoute(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun VisitBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (AppRoute) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavPill(
                icon = Icons.Default.Face,
                label = "Моя визитка",
                selected = currentDestination?.hasRoute<AppRoute.MyProfile>() == true,
                onClick = { onNavigate(AppRoute.MyProfile) }
            )
            Box(modifier = Modifier.size(56.dp))
            NavPill(
                icon = Icons.Default.Person,
                label = "Знакомства",
                selected = currentDestination?.hasRoute<AppRoute.Contacts>() == true,
                onClick = { onNavigate(AppRoute.Contacts) }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onNavigate(AppRoute.Scanner) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Сканировать",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun NavPill(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xs, horizontal = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (selected) MaterialTheme.colorScheme.primaryContainer
                    else androidx.compose.ui.graphics.Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
