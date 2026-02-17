package com.paradox543.malankaraorthodoxliturgica.domain.settings.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppLanguageTest {
    @Test
    fun `fromCode returns correct enum for valid codes`() {
        assertEquals(AppLanguage.MALAYALAM, AppLanguage.fromCode("ml"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.MANGLISH, AppLanguage.fromCode("mn"))
        assertEquals(AppLanguage.INDIC, AppLanguage.fromCode("indic"))
    }

    @Test
    fun `fromCode returns null for invalid codes`() {
        assertNull(AppLanguage.fromCode("invalid"))
        assertNull(AppLanguage.fromCode(""))
        assertNull(AppLanguage.fromCode("fr"))
        assertNull(AppLanguage.fromCode("ML")) // Case-sensitive
    }

    @Test
    fun `properLanguageMapper returns malayalam code for MALAYALAM`() {
        assertEquals("ml", AppLanguage.MALAYALAM.properLanguageMapper())
    }

    @Test
    fun `properLanguageMapper returns english code for ENGLISH`() {
        assertEquals("en", AppLanguage.ENGLISH.properLanguageMapper())
    }

    @Test
    fun `properLanguageMapper returns english code for MANGLISH fallback`() {
        assertEquals("en", AppLanguage.MANGLISH.properLanguageMapper())
    }

    @Test
    fun `properLanguageMapper returns english code for INDIC fallback`() {
        assertEquals("en", AppLanguage.INDIC.properLanguageMapper())
    }
}
