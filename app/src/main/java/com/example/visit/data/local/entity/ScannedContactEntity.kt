package com.example.visit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.visit.domain.model.Profile

@Entity(tableName = "scanned_contacts")
data class ScannedContactEntity(
    @PrimaryKey(true)
    val id: Long,
    val profile: Profile,
    val scannedAt: Long,
    val note: String?
)