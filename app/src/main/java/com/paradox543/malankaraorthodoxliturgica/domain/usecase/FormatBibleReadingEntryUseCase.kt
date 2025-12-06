package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.repository.BibleRepository
import javax.inject.Inject

class FormatBibleReadingEntryUseCase @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val formatBibleRangeUseCase: FormatBibleRangeUseCase,
) {
    operator fun invoke(
        entry: BibleReference,
        language: AppLanguage,
    ): String {
        val bookName = bibleRepository.getBibleBookName(entry.bookNumber - 1, language)

        val formattedRanges =
            entry.ranges.joinToString(", ") { range ->
                formatBibleRangeUseCase(range)
            }

        return "$bookName $formattedRanges"
    }
}
