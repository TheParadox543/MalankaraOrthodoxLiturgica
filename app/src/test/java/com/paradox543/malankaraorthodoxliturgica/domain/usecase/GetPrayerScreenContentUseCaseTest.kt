package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.repository.PrayerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPrayerScreenContentUseCaseTest {
    private class FakePrayerRepository(
        private val map: Map<String, List<PrayerElementDomain>>,
    ) : PrayerRepository {
        override fun loadTranslations(language: AppLanguage): Map<String, String> = emptyMap()

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
                object : com.paradox543.malankaraorthodoxliturgica.domain.repository.CalendarRepository {
                    override suspend fun initialize() {}

                    override fun getEventsForDate(date: java.time.LocalDate) =
                        emptyList<com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetails>()

                    override fun checkMonthDataExists(
                        month: Int,
                        year: Int,
                    ) = true

                    override fun loadMonthData(
                        month: Int?,
                        year: Int?,
                    ) = emptyList<com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeek>()

                    override fun getUpcomingWeekEvents() = emptyList<com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDay>()

                    override fun getUpcomingWeekEventItems() =
                        emptyList<com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetails>()
                }

            val dynamicUseCase = GetDynamicSongsUseCase(prayerRepo, calendarFake)

            val useCase = GetPrayerScreenContentUseCase(prayerRepo, dynamicUseCase)

            val result = useCase("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElementDomain.Title && it.content == "Linked Title" })
        }
}
