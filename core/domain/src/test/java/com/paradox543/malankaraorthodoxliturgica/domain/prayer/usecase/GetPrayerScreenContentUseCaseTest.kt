package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeCalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakePrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPrayerScreenContentUseCaseTest {
    @Test
    fun inlinesLinkAndCollapsibleProperly() =
        runBlocking {
            val linked =
                listOf(
                    PrayerElementDomain.Title("Linked Title"),
                    PrayerElementDomain.Prose("Some prose"),
                )

            val elementsMap =
                mapOf(
                    "main.json" to listOf(PrayerElementDomain.Link("linked.json")),
                    "linked.json" to linked,
                )

            val prayerRepo = FakePrayerRepository(elementsMap = elementsMap)
            val calendarRepo = FakeCalendarRepository()

            val dynamicUseCase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)
            val useCase = GetPrayerScreenContentUseCase(prayerRepo, dynamicUseCase)

            val result = useCase("main.json", AppLanguage.ENGLISH)

            assertTrue(result.any { it is PrayerElementDomain.Title && it.content == "Linked Title" })
        }
}
