package com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatBibleRangeUseCaseTest {
    private val useCase = FormatBibleRangeUseCase()

    @Test
    fun `formats single verse with same start and end`() {
        val range = ReferenceRange(startChapter = 5, startVerse = 16, endChapter = 5, endVerse = 16)
        assertEquals("5:16", useCase(range))
    }

    @Test
    fun `formats verse range within the same chapter`() {
        val range = ReferenceRange(startChapter = 5, startVerse = 1, endChapter = 5, endVerse = 10)
        assertEquals("5:1-10", useCase(range))
    }

    @Test
    fun `formats range spanning multiple chapters`() {
        val range = ReferenceRange(startChapter = 3, startVerse = 16, endChapter = 4, endVerse = 5)
        assertEquals("3:16 - 4:5", useCase(range))
    }

    @Test
    fun `formats first verse of first chapter`() {
        val range = ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 1)
        assertEquals("1:1", useCase(range))
    }
}
