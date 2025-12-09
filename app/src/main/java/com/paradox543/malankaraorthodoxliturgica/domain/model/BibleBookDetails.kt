package com.paradox543.malankaraorthodoxliturgica.domain.model

/**
 * Holds metadata for a single book of the Bible used throughout the app.
 *
 * This data class aggregates the book identity, storage/folder information,
 * chapter and verse counts, optional display text and preface content. It is
 * intended to be a lightweight value object passed between repository, use
 * cases and UI layers.
 *
 * @property book the canonical book identifier (see [BibleBookName]).
 * @property folder the folder name under which the book's resources (JSON, text)
 *   are stored. Typically corresponds to a package or assets folder.
 * @property chapters total number of chapters in this book.
 * @property verseCount list where each entry is the number of verses in the
 *   corresponding chapter (1-based chapter index corresponds to element 0).
 * @property category optional human-readable category (for example: "Old
 *   Testament", "Gospels", "Epistles"). Used for grouping in lists.
 * @property prefaces optional preface or introductory text for this book. If
 *   present, this content can be displayed before chapter 1.
 * @property displayTitle optional localized or formatted display title for the
 *   book. When null the UI should fall back to a default derived from [book].
 * @property ordinal optional displayable ordinal (for example "1", "II") used
 *   by some UI lists to show the book position.
 */
data class BibleBookDetails(
    val book: BibleBookName,
    val folder: String,
    val chapters: Int,
    val verseCount: List<Int>,
    val category: String? = null,
    val prefaces: PrefaceContent? = null,
    val displayTitle: DisplayText? = null,
    val ordinal: DisplayText? = null,
)