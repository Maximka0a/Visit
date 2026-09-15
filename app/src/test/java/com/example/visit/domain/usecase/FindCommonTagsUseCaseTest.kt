package com.example.visit.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class FindCommonTagsUseCaseTest {

    private val useCase = FindCommonTagsUseCase()

    @Test
    fun `returns common tags ignoring case and spaces`() {
        val result = useCase(
            myTags = listOf("Kotlin", " android ", "Compose"),
            otherTags = listOf("kotlin", "java")
        )

        assertEquals(listOf("kotlin"), result)
    }

    @Test
    fun `returns empty list when there are no common tags`() {
        val result = useCase(
            myTags = listOf("kotlin"),
            otherTags = listOf("java")
        )

        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `returns empty list when either list is empty`() {
        assertEquals(emptyList<String>(), useCase(emptyList(), listOf("kotlin")))
        assertEquals(emptyList<String>(), useCase(listOf("kotlin"), emptyList()))
    }
}
