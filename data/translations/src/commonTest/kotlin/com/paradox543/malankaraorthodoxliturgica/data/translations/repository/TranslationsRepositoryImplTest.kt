package com.paradox543.malankaraorthodoxliturgica.data.translations.repository

import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.RawTranslationsSource
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TranslationsRepositoryImplTest {
    @Test
    fun loadsRawOnlyOnceForRepeatedRequests() =
        runBlocking {
            val source = CountingRawTranslationsSource(sampleRawTranslations())
            val repository = TranslationsRepositoryImpl(source)

            repository.loadTranslations(AppLanguage.MALAYALAM)
            repository.loadTranslations(AppLanguage.MALAYALAM)
            repository.loadTranslations(AppLanguage.ENGLISH)

            assertEquals(1, source.loadCallCount)
        }

    @Test
    fun sharesEnglishCacheForEnglishManglishIndic() =
        runBlocking {
            val repository = TranslationsRepositoryImpl(CountingRawTranslationsSource(sampleRawTranslations()))

            val english = repository.loadTranslations(AppLanguage.ENGLISH)
            val manglish = repository.loadTranslations(AppLanguage.MANGLISH)
            val indic = repository.loadTranslations(AppLanguage.INDIC)

            assertSame(english, manglish)
            assertSame(english, indic)
            assertEquals("Hello", english["greeting"])
        }

    @Test
    fun keepsMalayalamCacheSeparateFromEnglishFamily() =
        runBlocking {
            val repository = TranslationsRepositoryImpl(CountingRawTranslationsSource(sampleRawTranslations()))

            val malayalam = repository.loadTranslations(AppLanguage.MALAYALAM)
            val english = repository.loadTranslations(AppLanguage.ENGLISH)

            assertEquals("Namaskaram", malayalam["greeting"])
            assertEquals("Hello", english["greeting"])
            if (malayalam === english) {
                error("Malayalam and English caches should not point to the same map instance")
            }
        }

    @Test
    fun concurrentRequestsStillReadRawOnlyOnce() =
        runBlocking {
            val source = CountingRawTranslationsSource(sampleRawTranslations())
            val repository = TranslationsRepositoryImpl(source)

            val requests =
                listOf(
                    async { repository.loadTranslations(AppLanguage.ENGLISH) },
                    async { repository.loadTranslations(AppLanguage.MANGLISH) },
                    async { repository.loadTranslations(AppLanguage.INDIC) },
                    async { repository.loadTranslations(AppLanguage.MALAYALAM) },
                )

            requests.awaitAll()

            assertEquals(1, source.loadCallCount)
        }

    private fun sampleRawTranslations(): Map<String, Map<String, String>> =
        mapOf(
            "greeting" to
                mapOf(
                    "en" to "Hello",
                    "ml" to "Namaskaram",
                ),
            "farewell" to
                mapOf(
                    "en" to "Goodbye",
                    "ml" to "Pokam",
                ),
        )

    private class CountingRawTranslationsSource(
        private val raw: Map<String, Map<String, String>>,
    ) : RawTranslationsSource {
        var loadCallCount: Int = 0
            private set

        override fun loadRawTranslations(): Map<String, Map<String, String>> {
            loadCallCount += 1
            return raw
        }
    }
}
