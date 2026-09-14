package com.example.visit.domain.usecase

import javax.inject.Inject

class FindCommonTagsUseCase @Inject  constructor() {
    operator fun invoke(myTags: List<String>, otherTags: List<String>): List<String> {
        val otherTagsNormalized = otherTags.map { it.trim().lowercase() }.toSet()

        return myTags
            .map { it.trim().lowercase() }
            .filter { it in otherTagsNormalized }
            .distinct()
    }
}