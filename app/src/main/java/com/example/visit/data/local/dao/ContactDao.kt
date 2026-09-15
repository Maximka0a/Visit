package com.example.visit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.visit.data.local.entity.ScannedContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Update
    suspend fun updateContact(contact: ScannedContactEntity)

    @Query("SELECT * FROM scanned_contacts")
    fun observeContact(): Flow<List<ScannedContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContact(contact: ScannedContactEntity)

    @Query("UPDATE scanned_contacts SET note = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String)

    @Query("DELETE FROM scanned_contacts WHERE id = :id")
    suspend fun deleteContact(id: Long)
}
