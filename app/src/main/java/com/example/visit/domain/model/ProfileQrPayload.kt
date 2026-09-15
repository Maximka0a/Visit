package com.example.visit.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileQrPayload(
    val name: String,
    val title: String?,
    val tags: List<String>,
    val socialLinks: Map<String, String>
)
