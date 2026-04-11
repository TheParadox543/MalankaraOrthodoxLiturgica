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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BibleViewModel(
    private val bibleRepository: BibleRepository,
    private val settingsRepository: SettingsRepository,
    private val getAdjacentChaptersUseCase: GetAdjacentChaptersUseCase,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.MALAYALAM,
        )

    private val _bibleBooks = MutableStateFlow<List<BibleBookDetails>>(emptyList())
    val bibleBooks: StateFlow<List<BibleBookDetails>> = _bibleBooks.asStateFlow()

    private val _chapter = MutableStateFlow<BibleChapter?>(null)
    val chapter: StateFlow<BibleChapter?> = _chapter.asStateFlow()

    private val _adjacent = MutableStateFlow<Pair<BibleChapterRef?, BibleChapterRef?>>(null to null)
    val adjacent: StateFlow<Pair<BibleChapterRef?, BibleChapterRef?>> = _adjacent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadBooksIfNeeded()
    }

    fun loadBooksIfNeeded() {
        if (_bibleBooks.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val books = withContext(backgroundDispatcher) { bibleRepository.loadBibleMetaData() }
                _bibleBooks.value = books
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to load Bible books."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadChapter(
        bookIndex: Int,
        chapterIndex: Int,
        language: AppLanguage,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result =
                    withContext(backgroundDispatcher) {
                        val chapterData = bibleRepository.loadBibleChapter(bookIndex, chapterIndex, language)
                        val adj = getAdjacentChaptersUseCase(bookIndex, chapterIndex)
                        chapterData to adj
                    }
                _chapter.value = result.first
                _adjacent.value = result.second
            } catch (t: Throwable) {
                _chapter.value = null
                _adjacent.value = null to null
                _error.value = t.message ?: "Failed to load chapter."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getBookOrNull(bookIndex: Int): BibleBookDetails? = _bibleBooks.value.getOrNull(bookIndex)
}