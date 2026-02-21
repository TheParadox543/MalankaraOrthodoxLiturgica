package com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping

import kotlin.test.Test
import kotlin.test.assertEquals

class ApplyPrayerReplacementsTest {
    @Test
    fun `replaces slash-t with four spaces`() {
        assertEquals("    indented", "/tindented".applyPrayerReplacements())
    }

    @Test
    fun `replaces multiple slash-t occurrences`() {
        assertEquals("    a    b", "/ta/tb".applyPrayerReplacements())
    }

    @Test
    fun `replaces slash-u200b with zero-width space character`() {
        val result = "/u200btest".applyPrayerReplacements()
        assertEquals("\u200btest", result)
    }

    @Test
    fun `replaces both tokens in the same string`() {
        val input = "/tHello/u200bWorld"
        val result = input.applyPrayerReplacements()
        assertEquals("    Hello\u200bWorld", result)
    }

    @Test
    fun `returns string unchanged when no tokens present`() {
        val input = "Plain text with no tokens."
        assertEquals(input, input.applyPrayerReplacements())
    }

    @Test
    fun `handles empty string`() {
        assertEquals("", "".applyPrayerReplacements())
    }
}
