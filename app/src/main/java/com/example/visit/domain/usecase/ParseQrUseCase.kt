package com.example.visit.domain.usecase

import com.example.visit.domain.model.Profile
import com.example.visit.domain.model.ProfileQrPayload
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ParseQrUseCase @Inject constructor() {
    operator fun invoke(rawText: String): Result<Profile> {
        return runCatching {
            val payload = Json.decodeFromString<ProfileQrPayload>(rawText)
            Profile(
                name = payload.name,
                title = payload.title,
                tags = payload.tags,
                socialLinks = payload.socialLinks,
                themeId = 0
            )
        }
    }
}
