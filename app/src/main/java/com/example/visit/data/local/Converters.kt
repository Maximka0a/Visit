package com.example.visit.data.local

import androidx.room.TypeConverter
import com.example.visit.domain.model.Profile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Converters {
    @TypeConverter
    fun profileToJson(profile: Profile): String{
        return Json.encodeToString(profile)
    }
    @TypeConverter
    fun JsonToProfile(json: String): Profile{
        return Json.decodeFromString(json)
    }
}