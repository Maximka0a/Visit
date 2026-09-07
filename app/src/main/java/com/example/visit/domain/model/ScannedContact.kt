package com.example.visit.domain.model

data class ScannedContact(
    val id: Long,
    val profile: Profile,
    val scannedAt: Long,
    val note: String?,
)
