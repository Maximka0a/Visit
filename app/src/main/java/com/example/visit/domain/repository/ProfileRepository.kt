package com.example.visit.domain.repository

import com.example.visit.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<Profile?>
    suspend fun saveProfile(profile: Profile)
}