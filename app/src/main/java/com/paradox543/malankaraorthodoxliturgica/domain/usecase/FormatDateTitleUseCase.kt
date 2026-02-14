package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import java.time.LocalDate
import javax.inject.Inject

class FormatDateTitleUseCase @Inject constructor() {
    operator fun invoke(
        event: LiturgicalEventDetails,
        selectedLanguage: AppLanguage,
    ): String {
        val currentYear = LocalDate.now().year
        return event.startedYear?.let { startedYear ->
            val yearNumber = currentYear - startedYear + 1
            val baseYearString = "$yearNumber"

            if (selectedLanguage == AppLanguage.MALAYALAM && event.title.ml != null) {
                "$baseYearString-ാം${event.title.ml}"
            } else {
                "$baseYearString${generateYearSuffix(yearNumber)} ${event.title.en}"
            }
        }  ?: when (selectedLanguage) {
            AppLanguage.MALAYALAM -> event.title.ml ?: event.title.en
            else -> event.title.en
        }
    }

    private fun generateYearSuffix(year: Int): String =
        when (year % 10) {
            1 -> if (year % 100 != 11) "st" else "th"
            2 -> if (year % 100 != 12) "nd" else "th"
            3 -> if (year % 100 != 13) "rd" else "th"
            else -> "th"
        }
}
