package com.example.visit.domain.repository

import com.example.visit.domain.model.ScannedContact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun observeContact(): Flow<List<ScannedContact>>
    suspend fun saveContact(contact: ScannedContact)
    suspend fun updateNote(id: Long, note: String)
    suspend fun deleteContact(id: Long)
}