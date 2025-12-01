package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleReference
import javax.inject.Inject

class FormatGospelEntryUseCase @Inject constructor(
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
