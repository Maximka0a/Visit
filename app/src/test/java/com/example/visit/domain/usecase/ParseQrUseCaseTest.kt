package com.example.visit.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ParseQrUseCaseTest {

    private val useCase = ParseQrUseCase()

    @Test
    fun `parses valid qr payload into a profile`() {
        val json = """
            {"name":"Alice","title":"Developer","tags":["kotlin"],"socialLinks":{"telegram":"alice"}}
        """.trimIndent()

        val result = useCase(json)

        assertTrue(result.isSuccess)
        val profile = result.getOrThrow()
        assertEquals("Alice", profile.name)
        assertEquals("Developer", profile.title)
        assertEquals(listOf("kotlin"), profile.tags)
        assertEquals(mapOf("telegram" to "alice"), profile.socialLinks)
    }

    @Test
    fun `returns failure for invalid text`() {
        val result = useCase("not a valid qr code")

        assertTrue(result.isFailure)
    }
}
