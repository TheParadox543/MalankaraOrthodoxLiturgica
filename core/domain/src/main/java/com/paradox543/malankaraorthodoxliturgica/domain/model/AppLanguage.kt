package com.paradox543.malankaraorthodoxliturgica.domain.model

/**
 * Supported application languages.
 *
 * Each enum entry carries a short language code used for resource lookup and a
 * human-readable display name shown in the UI. Typical codes:
 *  - "ml" = Malayalam
 *  - "en" = English
 *  - "mn" = Manglish (Malayalam in English script)
 *  - "indic" = Indic (Malayalam in Indic script)
 *
 * Usage examples:
 *  - `AppLanguage.fromCode("ml")` to parse a stored preference
 *  - `language.displayName` to show the language name in the settings UI
 *  - `language.properLanguageMapper()` to obtain the canonical resource
 *    language code used by the app when Manglish/Indic fallback to English.
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
) {
    MALAYALAM("ml", "മലയാളം"),
    ENGLISH("en", "English"),
    MANGLISH("mn", "Manglish (Malayalam in English Script)"),
    INDIC("indic", "Indic (Malayalam in Indic Script)"),
    ;

    companion object {
        /**
         * Returns the enum entry matching the provided language code, or null
         * if no matching language exists.
         *
         * This is a convenience for parsing persisted preferences or external
         * input where only the short code is available.
         */
        fun fromCode(code: String): AppLanguage? = entries.find { it.code == code }
    }

    /**
     * Returns the canonical language code to use for resource lookups.
     *
     * Some languages in the app (e.g., Manglish and Indic) fall back to
     * English resources at present; this helper returns the code that should
     * be used to map to the correct resource bundle.
     */
    fun properLanguageMapper(): String =
        when (this) {
            MALAYALAM -> this.code
            ENGLISH -> this.code
            MANGLISH -> ENGLISH.code
            INDIC -> ENGLISH.code
        }
}