package com.example.visit.data.repository

import com.example.visit.data.datastore.ProfileDataStore
import com.example.visit.domain.model.Profile
import com.example.visit.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(private val profileDataStore: ProfileDataStore):
    ProfileRepository {
    override fun observeProfile(): Flow<Profile?> {
        return profileDataStore.observeProfile()
    }

    override suspend fun saveProfile(profile: Profile) {
        profileDataStore.saveProfile(profile)
    }

}