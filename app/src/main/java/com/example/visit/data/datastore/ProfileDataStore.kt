package com.example.visit.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.visit.domain.model.Profile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Profile")

@Singleton
class ProfileDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val profileKey = stringPreferencesKey("profile")

    fun observeProfile(): Flow<Profile?> {
        val result: Flow<Profile?> = context.dataStore.data.map {
            preferences ->
            if (preferences[profileKey] !== null){
                Json.decodeFromString<Profile>(preferences[profileKey].toString())
            }else{
                null
            }
        }
        return result
    }

    suspend fun saveProfile(profile: Profile){
        context.dataStore.edit {
            preferences ->
            preferences[profileKey] = Json.encodeToString(profile)
        }
    }
}
