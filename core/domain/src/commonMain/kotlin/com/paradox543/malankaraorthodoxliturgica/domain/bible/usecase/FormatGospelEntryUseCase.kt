package com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

class FormatGospelEntryUseCase(
    private val formatBibleReadingEntryUseCase: FormatBibleReadingEntryUseCase,
) {
    operator fun invoke(
        entries: List<BibleReference>,
        language: AppLanguage,
    ): String {
        if (entries.isEmpty()) return ""

        return entries.joinToString(", ") { entry ->
            formatBibleReadingEntryUseCase(entry, language)
        }
    }
}
