package com.paradox543.malankaraorthodoxliturgica.domain.calendar.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.TitleStr
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.time.LocalDate

class FormatDateTitleUseCaseTest {
    private val useCase = FormatDateTitleUseCase()
    private val currentYear = LocalDate.now().year

    private fun makeEvent(
        en: String,
        ml: String? = null,
        startedYear: Int? = null,
    ) = LiturgicalEventDetails(
        type = "feast",
        title = TitleStr(en = en, ml = ml),
        startedYear = startedYear,
    )

    @Test
    fun `returns plain English title when no startedYear and language is ENGLISH`() {
        val event = makeEvent(en = "Christmas")
        assertEquals("Christmas", useCase(event, AppLanguage.ENGLISH))
    }

    @Test
    fun `returns Malayalam title when no startedYear and language is MALAYALAM`() {
        val event = makeEvent(en = "Christmas", ml = "ക്രിസ്മസ്")
        assertEquals("ക്രിസ്മസ്", useCase(event, AppLanguage.MALAYALAM))
    }

    @Test
    fun `falls back to English when no startedYear and Malayalam title is null`() {
        val event = makeEvent(en = "Christmas", ml = null)
        assertEquals("Christmas", useCase(event, AppLanguage.MALAYALAM))
    }

    @Test
    fun `formats English title with correct ordinal suffix for 1st`() {
        val event = makeEvent(en = "Feast", startedYear = currentYear)
        val result = useCase(event, AppLanguage.ENGLISH)
        assertTrue(result.startsWith("1st"), "Expected '1st Feast' but got '$result'")
    }

    @Test
    fun `formats English title with correct ordinal suffix for 2nd`() {
        val event = makeEvent(en = "Feast", startedYear = currentYear - 1)
        val result = useCase(event, AppLanguage.ENGLISH)
        assertTrue(result.startsWith("2nd"), "Expected '2nd Feast' but got '$result'")
    }

    @Test
    fun `formats English title with correct ordinal suffix for 3rd`() {
        val event = makeEvent(en = "Feast", startedYear = currentYear - 2)
        val result = useCase(event, AppLanguage.ENGLISH)
        assertTrue(result.startsWith("3rd"), "Expected '3rd Feast' but got '$result'")
    }

    @Test
    fun `formats English title with th suffix for 4th and beyond`() {
        val event = makeEvent(en = "Feast", startedYear = currentYear - 3)
        val result = useCase(event, AppLanguage.ENGLISH)
        assertTrue(result.startsWith("4th"), "Expected '4th Feast' but got '$result'")
    }

    @Test
    fun `uses th suffix for 11th, 12th, 13th (special cases)`() {
        // 11th: startedYear = currentYear - 10
        val event11 = makeEvent(en = "Feast", startedYear = currentYear - 10)
        val result11 = useCase(event11, AppLanguage.ENGLISH)
        assertTrue(result11.startsWith("11th"), "Expected '11th' but got '$result11'")

        // 12th: startedYear = currentYear - 11
        val event12 = makeEvent(en = "Feast", startedYear = currentYear - 11)
        val result12 = useCase(event12, AppLanguage.ENGLISH)
        assertTrue(result12.startsWith("12th"), "Expected '12th' but got '$result12'")

        // 13th: startedYear = currentYear - 12
        val event13 = makeEvent(en = "Feast", startedYear = currentYear - 12)
        val result13 = useCase(event13, AppLanguage.ENGLISH)
        assertTrue(result13.startsWith("13th"), "Expected '13th' but got '$result13'")
    }

    @Test
    fun `formats Malayalam title with year prefix`() {
        val event = makeEvent(en = "Feast", ml = "ഉത്സവം", startedYear = currentYear)
        val result = useCase(event, AppLanguage.MALAYALAM)
        assertTrue(result.contains("ഉത്സവം"), "Expected result to contain 'ഉത്സവം' but got '$result'")
        assertTrue(result.startsWith("1"), "Expected result to start with '1' but got '$result'")
    }

    @Test
    fun `falls back to English title for Malayalam when ml is null and startedYear is set`() {
        val event = makeEvent(en = "Feast", ml = null, startedYear = currentYear)
        val result = useCase(event, AppLanguage.MALAYALAM)
        // When ml is null, should fall through to English path
        assertTrue(result.contains("Feast"), "Expected result to contain 'Feast' but got '$result'")
    }
}
