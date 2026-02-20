package com.paradox543.malankaraorthodoxliturgica.domain.bible.model

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class DisplayTextTest {
    @Test
    fun `get returns Malayalam text for MALAYALAM when ml is provided`() {
        val text = DisplayText(en = "Chapter", ml = "അദ്ധ്യായം")
        assertEquals("അദ്ധ്യായം", text.get(AppLanguage.MALAYALAM))
    }

    @Test
    fun `get falls back to English for MALAYALAM when ml is null`() {
        val text = DisplayText(en = "Chapter", ml = null)
        assertEquals("Chapter", text.get(AppLanguage.MALAYALAM))
    }

    @Test
    fun `get returns English text for ENGLISH language`() {
        val text = DisplayText(en = "Chapter", ml = "അദ്ധ്യായം")
        assertEquals("Chapter", text.get(AppLanguage.ENGLISH))
    }

    @Test
    fun `get returns English text for MANGLISH language`() {
        val text = DisplayText(en = "Chapter", ml = "അദ്ധ്യായം")
        assertEquals("Chapter", text.get(AppLanguage.MANGLISH))
    }

    @Test
    fun `get returns English text for INDIC language`() {
        val text = DisplayText(en = "Chapter", ml = "അദ്ധ്യായം")
        assertEquals("Chapter", text.get(AppLanguage.INDIC))
    }

    @Test
    fun `get handles empty Malayalam string by using it`() {
        val text = DisplayText(en = "Chapter", ml = "")
        assertEquals("", text.get(AppLanguage.MALAYALAM))
    }
}
