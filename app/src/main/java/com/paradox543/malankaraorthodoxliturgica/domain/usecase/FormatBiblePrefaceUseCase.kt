package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrefaceContent
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.repository.BibleRepository
import javax.inject.Inject

/**
 * Formats the preface content for a given BibleReference and language.
 * This extracts preface templates from the repository and substitutes placeholders.
 */
class FormatBiblePrefaceUseCase @Inject constructor(
    private val bibleRepository: BibleRepository,
) {
    operator fun invoke(
        bibleReference: BibleReference,
        language: AppLanguage,
    ): List<PrayerElementDomain>? {
        val books = bibleRepository.loadBibleMetaData()
        val book = books.getOrNull(bibleReference.bookNumber - 1) ?: return null
        val prefaces = bibleRepository.loadPrefaceTemplates()

        val prefaceContent: PrefaceContent =
            book.prefaces
                ?: when (book.category) {
                    "prophet" -> prefaces.prophets
                    "generalEpistle" -> prefaces.generalEpistle
                    "paulineEpistle" -> prefaces.paulineEpistle
                    else -> return null
                }

        val sourcePreface: List<PrayerElementDomain> =
            when (language) {
                AppLanguage.MALAYALAM -> prefaceContent.ml
                AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> prefaceContent.en
            }

        val title =
            when (language) {
                AppLanguage.MALAYALAM -> book.book.ml
                AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> book.book.en
            }

        val displayTitle =
            when (language) {
                AppLanguage.MALAYALAM -> book.displayTitle?.ml ?: ""
                AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> book.displayTitle?.en ?: ""
            }

        val ordinal =
            when (language) {
                AppLanguage.MALAYALAM -> book.ordinal?.ml ?: ""
                AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> book.ordinal?.en ?: ""
            }

        return sourcePreface.map { item ->
            when (item) {
                is PrayerElementDomain.Prose -> {
                    item.copy(
                        content =
                            item.content
                                .replace("{title}", title)
                                .replace("{displayTitle}", displayTitle)
                                .replace("{ordinal}", ordinal),
                    )
                }

                else -> {
                    item
                }
            }
        }
    }
}
