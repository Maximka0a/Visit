package com.example.visit.domain.usecase

import com.example.visit.domain.model.Profile
import com.example.visit.domain.model.ScannedContact
import javax.inject.Inject

class FindDuplicateContactUseCase @Inject constructor() {
    operator fun invoke(newProfile: Profile, existingContacts: List<ScannedContact>): ScannedContact? {

        val newLinks = newProfile.socialLinks.values.toSet()

        if (newLinks.isNotEmpty()) {
            val matchByLink = existingContacts.firstOrNull { existing ->
                val existingLinks = existing.profile.socialLinks.values.toSet()
                existingLinks.intersect(newLinks).isNotEmpty()
            }
            if (matchByLink != null) return matchByLink
        }

        val newNameNormalized = newProfile.name.trim().lowercase()
        val matchByName = existingContacts.firstOrNull { existing ->
            existing.profile.name.trim().lowercase() == newNameNormalized
        }

        return matchByName
    }
}