package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetailsData
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPrayerScreenContentUseCaseTest {
    private class FakePrayerRepository(
        private val map: Map<String, List<PrayerElementDomain>>,
    ) : PrayerRepository {
        override suspend fun loadPrayerElements(
            fileName: String,
            language: AppLanguage,
            currentDepth: Int,
        ): List<PrayerElementDomain> = map[fileName] ?: emptyList()
    }

    @Test
    fun `inlines link and collapsible properly`() =
        runBlocking {
            val linked =
                listOf(
                    PrayerElementDomain.Title("Linked Title"),
                    PrayerElementDomain.Prose("Some prose"),
                )

            val repoMap =
                mapOf(
                    "main.json" to listOf(PrayerElementDomain.Link("linked.json")),
                    "linked.json" to linked,
                )

            val prayerRepo = FakePrayerRepository(repoMap)

            val calendarFake =
                object : CalendarRepository {
                    override suspend fun initialize() {}

                    override fun getEventsForDate(date: java.time.LocalDate) = emptyList<LiturgicalEventDetailsData>()

                    override fun checkMonthDataExists(
                        month: Int,
                        year: Int,
                    ) = true

                    override fun loadMonthData(
                        month: Int?,
                        year: Int?,
                    ) = emptyList<CalendarWeek>()

                    override fun getUpcomingWeekEvents() = emptyList<CalendarDay>()

                    override fun getUpcomingWeekEventItems() = emptyList<LiturgicalEventDetailsData>()
                }

            val dynamicUseCase = GetDynamicSongsUseCase(prayerRepo, calendarFake)

            val useCase = GetPrayerScreenContentUseCase(prayerRepo, dynamicUseCase)

            val result = useCase("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElementDomain.Title && it.content == "Linked Title" })
        }
}
