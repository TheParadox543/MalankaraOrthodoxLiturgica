package com.paradox543.malankaraorthodoxliturgica.domain.model

/**
 * A reference to one or more ranges within a Bible book.
 *
 * The [bookNumber] identifies the canonical book (index into the app's
 * metadata). [ranges] contains one or more contiguous ranges (chapter and
 * verse bounds) describing the referenced passages.
 *
 * @property bookNumber zero-based index of the book in the app's Bible
 *   metadata list.
 * @property ranges list of [ReferenceRange] entries describing chapter/verse
 *   intervals for this reference.
 */
data class BibleReference(
    val bookNumber: Int,
    val ranges: List<ReferenceRange>,
)