package com.paradox543.malankaraorthodoxliturgica.domain.bible.model

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class BibleBookNameTest {
    @Test
    fun `get returns Malayalam name for MALAYALAM language`() {
        val bookName = BibleBookName(en = "Genesis", ml = "ഉല്പത്തി")
        assertEquals("ഉല്പത്തി", bookName.get(AppLanguage.MALAYALAM))
    }

    @Test
    fun `get returns English name for ENGLISH language`() {
        val bookName = BibleBookName(en = "Genesis", ml = "ഉല്പത്തി")
        assertEquals("Genesis", bookName.get(AppLanguage.ENGLISH))
    }

    @Test
    fun `get returns English name for MANGLISH language`() {
        val bookName = BibleBookName(en = "Genesis", ml = "ഉല്പത്തി")
        assertEquals("Genesis", bookName.get(AppLanguage.MANGLISH))
    }

    @Test
    fun `get returns English name for INDIC language`() {
        val bookName = BibleBookName(en = "Genesis", ml = "ഉല്പത്തി")
        assertEquals("Genesis", bookName.get(AppLanguage.INDIC))
    }
}
