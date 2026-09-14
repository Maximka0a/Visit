package com.example.visit.domain.usecase

import com.example.visit.domain.model.Profile
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ParseQrUseCase @Inject  constructor( ){
    fun invoke(rawText: String): Result<Profile> {
        return runCatching {
            Json.decodeFromString<Profile>(rawText)
        }
    }
}