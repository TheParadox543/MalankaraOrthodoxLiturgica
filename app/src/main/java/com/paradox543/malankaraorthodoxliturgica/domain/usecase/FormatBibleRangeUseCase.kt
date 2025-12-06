package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.model.ReferenceRange
import javax.inject.Inject

/**
 * Formats a single BibleRange into a string (e.g., "5:1-10" or "3:16 - 4:5").
 * This is a helper function, not exposed directly to UI.
 */
class FormatBibleRangeUseCase @Inject constructor() {
    operator fun invoke(range: ReferenceRange): String =
        if (range.startChapter == range.endChapter) {
            if (range.startVerse == range.endVerse) {
                "${range.startChapter}:${range.startVerse}"
            } else {
                "${range.startChapter}:${range.startVerse}-${range.endVerse}"
            }
        } else {
            "${range.startChapter}:${range.startVerse} - " +
                "${range.endChapter}:${range.endVerse}"
        }
}
