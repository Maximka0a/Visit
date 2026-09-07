package com.example.visit.data.repository

import com.example.visit.data.local.dao.ContactDao
import com.example.visit.data.local.entity.ScannedContactEntity
import com.example.visit.domain.model.ScannedContact
import com.example.visit.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(private val contactDao: ContactDao): ContactsRepository {
    override fun observeContact(): Flow<List<ScannedContact>> {
        return contactDao.observeContact().map { list ->
            list.map { entity -> entity.toDomain()  }
        }
    }

    override suspend fun saveContact(contact: ScannedContact) {
        contactDao.saveContact(contact.toEntity())
    }

    override suspend fun updateNote(id: Long, note: String) {
        contactDao.updateNote(id,note)
    }

    override suspend fun deleteContact(id: Long) {
        contactDao.deleteContact(id)
    }

}

fun ScannedContactEntity.toDomain(): ScannedContact{
    return ScannedContact(
        id = id,
        profile = profile,
        scannedAt = scannedAt,
        note = note
    )
}

fun ScannedContact.toEntity(): ScannedContactEntity{
    return ScannedContactEntity(
        id = id,
        profile = profile,
        scannedAt = scannedAt,
        note = note
    )
}