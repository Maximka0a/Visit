package com.example.visit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.visit.presentation.ProfileScreen.EditProfileRoute
import com.example.visit.presentation.ProfileScreen.EditProfileScreen
import com.example.visit.presentation.myCardScreen.MyCardRoute
import kotlinx.serialization.Serializable

sealed interface ProfileScreen {
    @Serializable
    data object MyProfile : ProfileScreen
    @Serializable
    data object EditProfile : ProfileScreen
}

@Composable
fun NavigationVisit(

){
    val navController = rememberNavController()

    NavHost(navController, startDestination = ProfileScreen.EditProfile) {
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
    }
}