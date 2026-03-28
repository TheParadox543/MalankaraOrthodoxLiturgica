package com.paradox543.malankaraorthodoxliturgica.feature.bible.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapterRef
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.GetAdjacentChaptersUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BibleViewModel(
    private val bibleRepository: BibleRepository,
    private val settingsRepository: SettingsRepository,
    private val getAdjacentChaptersUseCase: GetAdjacentChaptersUseCase,
) : ViewModel() {
    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.MALAYALAM,
        )

    val bibleBooks: List<BibleBookDetails> by lazy(LazyThreadSafetyMode.NONE) { bibleRepository.loadBibleMetaData() }

    fun loadBibleBook(bookNumber: Int): BibleBookDetails = bibleBooks[bookNumber]

    fun loadBibleChapter(
        bookNumber: Int,
        chapterNumber: Int,
        language: AppLanguage,
    ): BibleChapter? = bibleRepository.loadBibleChapter(bookNumber, chapterNumber, language)

    fun getAdjacentChapters(
        bookIndex: Int,
        chapterIndex: Int,
    ): Pair<BibleChapterRef?, BibleChapterRef?> = getAdjacentChaptersUseCase(bookIndex, chapterIndex)
}