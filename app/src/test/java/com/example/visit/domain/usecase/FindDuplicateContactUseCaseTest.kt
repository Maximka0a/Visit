package com.example.visit.domain.usecase

import com.example.visit.domain.model.Profile
import com.example.visit.domain.model.ScannedContact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FindDuplicateContactUseCaseTest {

    private val useCase = FindDuplicateContactUseCase()

    private fun profile(
        name: String,
        socialLinks: Map<String, String> = emptyMap()
    ) = Profile(name = name, title = null, tags = emptyList(), socialLinks = socialLinks, themeId = 0)

    private fun contact(id: Long, profile: Profile) =
        ScannedContact(id = id, profile = profile, scannedAt = 0L, note = null)

    @Test
    fun `finds duplicate by matching social link`() {
        val existing = listOf(
            contact(1, profile("Alice", mapOf("telegram" to "alice_tg")))
        )
        val newProfile = profile("Alice Smith", mapOf("telegram" to "alice_tg"))

        val result = useCase(newProfile, existing)

        assertEquals(1L, result?.id)
    }

    @Test
    fun `finds duplicate by matching name when no links match`() {
        val existing = listOf(
            contact(1, profile("Alice"))
        )
        val newProfile = profile(" alice ")

        val result = useCase(newProfile, existing)

        assertEquals(1L, result?.id)
    }

    @Test
    fun `returns null when nothing matches`() {
        val existing = listOf(
            contact(1, profile("Alice"))
        )
        val newProfile = profile("Bob")

        val result = useCase(newProfile, existing)

        assertNull(result)
    }
}
