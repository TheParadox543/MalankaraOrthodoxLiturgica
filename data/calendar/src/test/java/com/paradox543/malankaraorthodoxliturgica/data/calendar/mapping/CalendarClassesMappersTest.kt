package com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping

import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.BibleReadingsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.BibleReferenceDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarDayDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarWeekDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalEventDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.ReferenceRangeDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.TitleStrDto
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CalendarClassesMappersTest {

    // ─── TitleStrDto ─────────────────────────────────────────────────────────

    @Test
    fun `TitleStrDto toDomain maps en and ml`() {
        val dto = TitleStrDto(en = "Easter Sunday", ml = "ഉയിർപ്പ് ഞായർ")
        val domain = dto.toDomain()
        assertEquals("Easter Sunday", domain.en)
        assertEquals("ഉയിർപ്പ് ഞായർ", domain.ml)
    }

    @Test
    fun `TitleStrDto toDomain maps null ml as null`() {
        val dto = TitleStrDto(en = "Easter Sunday", ml = null)
        val domain = dto.toDomain()
        assertEquals("Easter Sunday", domain.en)
        assertNull(domain.ml)
    }

    // ─── ReferenceRangeDto ───────────────────────────────────────────────────

    @Test
    fun `ReferenceRangeDto toDomain maps all four fields`() {
        val dto = ReferenceRangeDto(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 10)
        val domain = dto.toDomain()
        assertEquals(1, domain.startChapter)
        assertEquals(1, domain.startVerse)
        assertEquals(1, domain.endChapter)
        assertEquals(10, domain.endVerse)
    }

    @Test
    fun `ReferenceRangeDto toDomain handles cross-chapter range`() {
        val dto = ReferenceRangeDto(startChapter = 3, startVerse = 16, endChapter = 4, endVerse = 5)
        val domain = dto.toDomain()
        assertEquals(3, domain.startChapter)
        assertEquals(16, domain.startVerse)
        assertEquals(4, domain.endChapter)
        assertEquals(5, domain.endVerse)
    }

    // ─── BibleReferenceDto ───────────────────────────────────────────────────

    @Test
    fun `BibleReferenceDto toDomain maps bookNumber and ranges`() {
        val dto = BibleReferenceDto(
            bookNumber = 43,
            ranges = listOf(ReferenceRangeDto(1, 1, 1, 17)),
        )
        val domain = dto.toDomain()
        assertEquals(43, domain.bookNumber)
        assertEquals(1, domain.ranges.size)
        assertEquals(1, domain.ranges[0].startChapter)
        assertEquals(17, domain.ranges[0].endVerse)
    }

    @Test
    fun `List of BibleReferenceDto toBibleReferenceDomain maps all items`() {
        val dtos = listOf(
            BibleReferenceDto(bookNumber = 40, ranges = listOf(ReferenceRangeDto(1, 1, 1, 25))),
            BibleReferenceDto(bookNumber = 41, ranges = listOf(ReferenceRangeDto(2, 1, 2, 12))),
        )
        val result = dtos.toBibleReferenceDomain()
        assertEquals(2, result.size)
        assertEquals(40, result[0].bookNumber)
        assertEquals(41, result[1].bookNumber)
    }

    @Test
    fun `List of BibleReferenceDto toBibleReferenceDomain returns empty list for empty input`() {
        assertEquals(emptyList(), emptyList<BibleReferenceDto>().toBibleReferenceDomain())
    }

    // ─── BibleReadingsDto ────────────────────────────────────────────────────

    @Test
    fun `BibleReadingsDto toDomain maps only gospel when others are null`() {
        val dto = BibleReadingsDto(
            gospel = listOf(BibleReferenceDto(bookNumber = 43, ranges = listOf(ReferenceRangeDto(3, 16, 3, 16)))),
        )
        val domain = dto.toDomain()
        assertNull(domain.vespersGospel)
        assertNull(domain.matinsGospel)
        assertNull(domain.primeGospel)
        assertNull(domain.oldTestament)
        assertNull(domain.generalEpistle)
        assertNull(domain.paulEpistle)
        assertEquals(1, domain.gospel?.size)
        assertEquals(43, domain.gospel?.get(0)?.bookNumber)
    }

    @Test
    fun `BibleReadingsDto toDomain maps all non-null reading types`() {
        val ref = BibleReferenceDto(bookNumber = 1, ranges = listOf(ReferenceRangeDto(1, 1, 1, 1)))
        val dto = BibleReadingsDto(
            vespersGospel = listOf(ref),
            matinsGospel = listOf(ref),
            primeGospel = listOf(ref),
            oldTestament = listOf(ref),
            generalEpistle = listOf(ref),
            paulEpistle = listOf(ref),
            gospel = listOf(ref),
        )
        val domain = dto.toDomain()
        assertEquals(1, domain.vespersGospel?.size)
        assertEquals(1, domain.matinsGospel?.size)
        assertEquals(1, domain.primeGospel?.size)
        assertEquals(1, domain.oldTestament?.size)
        assertEquals(1, domain.generalEpistle?.size)
        assertEquals(1, domain.paulEpistle?.size)
        assertEquals(1, domain.gospel?.size)
    }

    // ─── LiturgicalEventDetailsDto ───────────────────────────────────────────

    @Test
    fun `LiturgicalEventDetailsDto toDomain maps required fields`() {
        val dto = LiturgicalEventDetailsDto(
            type = "feast",
            title = TitleStrDto(en = "Easter", ml = "ഉയിർപ്പ്"),
        )
        val domain = dto.toDomain()
        assertEquals("feast", domain.type)
        assertEquals("Easter", domain.title.en)
        assertEquals("ഉയിർപ്പ്", domain.title.ml)
        assertNull(domain.bibleReadings)
        assertNull(domain.niram)
        assertNull(domain.specialSongsKey)
        assertNull(domain.startedYear)
    }

    @Test
    fun `LiturgicalEventDetailsDto toDomain maps all optional fields when present`() {
        val dto = LiturgicalEventDetailsDto(
            type = "feast",
            title = TitleStrDto(en = "Pentecost"),
            bibleReadings = BibleReadingsDto(
                gospel = listOf(BibleReferenceDto(43, listOf(ReferenceRangeDto(20, 19, 20, 23)))),
            ),
            niram = 3,
            specialSongsKey = "pentecost_songs",
            startedYear = 33,
        )
        val domain = dto.toDomain()
        assertEquals(3, domain.niram)
        assertEquals("pentecost_songs", domain.specialSongsKey)
        assertEquals(33, domain.startedYear)
        assertEquals(1, domain.bibleReadings?.gospel?.size)
    }

    @Test
    fun `List of LiturgicalEventDetailsDto toLiturgicalEventsDetailsDomain maps all items`() {
        val dtos = listOf(
            LiturgicalEventDetailsDto(type = "feast", title = TitleStrDto(en = "Easter")),
            LiturgicalEventDetailsDto(type = "fast", title = TitleStrDto(en = "Great Lent")),
        )
        val result = dtos.toLiturgicalEventsDetailsDomain()
        assertEquals(2, result.size)
        assertEquals("feast", result[0].type)
        assertEquals("fast", result[1].type)
    }

    // ─── CalendarDayDto ──────────────────────────────────────────────────────

    @Test
    fun `CalendarDayDto toDomain maps date and events`() {
        val date = LocalDate.of(2025, 4, 20)
        val dto = CalendarDayDto(
            date = date,
            events = listOf(LiturgicalEventDetailsDto(type = "feast", title = TitleStrDto(en = "Easter"))),
        )
        val domain = dto.toDomain()
        assertEquals(date, domain.date)
        assertEquals(1, domain.events.size)
        assertEquals("Easter", domain.events[0].title.en)
    }

    @Test
    fun `CalendarDayDto toDomain handles empty events list`() {
        val date = LocalDate.of(2025, 1, 1)
        val dto = CalendarDayDto(date = date, events = emptyList())
        val domain = dto.toDomain()
        assertEquals(date, domain.date)
        assertEquals(emptyList(), domain.events)
    }

    @Test
    fun `List of CalendarDayDto toCalendarDaysDomain maps all items`() {
        val dtos = listOf(
            CalendarDayDto(LocalDate.of(2025, 1, 1), emptyList()),
            CalendarDayDto(LocalDate.of(2025, 1, 2), emptyList()),
        )
        val result = dtos.toCalendarDaysDomain()
        assertEquals(2, result.size)
        assertEquals(LocalDate.of(2025, 1, 1), result[0].date)
        assertEquals(LocalDate.of(2025, 1, 2), result[1].date)
    }

    // ─── CalendarWeekDto ─────────────────────────────────────────────────────

    @Test
    fun `CalendarWeekDto toDomain maps days list`() {
        val days = (0..6).map { i ->
            CalendarDayDto(LocalDate.of(2025, 4, 13 + i), emptyList())
        }
        val dto = CalendarWeekDto(days = days)
        val domain = dto.toDomain()
        assertEquals(7, domain.days.size)
        assertEquals(LocalDate.of(2025, 4, 13), domain.days[0].date)
        assertEquals(LocalDate.of(2025, 4, 19), domain.days[6].date)
    }

    @Test
    fun `List of CalendarWeekDto toCalendarWeeksDomain maps all weeks`() {
        val week1 = CalendarWeekDto((0..6).map { CalendarDayDto(LocalDate.of(2025, 4, 13 + it), emptyList()) })
        val week2 = CalendarWeekDto((0..6).map { CalendarDayDto(LocalDate.of(2025, 4, 20 + it), emptyList()) })
        val result = listOf(week1, week2).toCalendarWeeksDomain()
        assertEquals(2, result.size)
        assertEquals(7, result[0].days.size)
        assertEquals(7, result[1].days.size)
    }
}
