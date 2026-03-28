package com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetAdjacentSiblingRoutesUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerNodesForCurrentTimeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PrayerNavViewModel(
    settingsRepository: SettingsRepository,
    private val prayerRepository: PrayerRepository,
    private val getAdjacentSiblingRoutesUseCase: GetAdjacentSiblingRoutesUseCase,
    private val getPrayerNodesForCurrentTimeUseCase: GetPrayerNodesForCurrentTimeUseCase,
    private val inAppReviewManager: InAppReviewManager,
) : ViewModel() {
    private val initialTree = PageNode(route = "root", parent = null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val rootNode =
        settingsRepository.language
            .mapLatest { lang ->
                prayerRepository.getPrayerNavigationTree(lang)
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                initialTree,
            )

    private val _currentNode = MutableStateFlow<PageNode?>(null)
    val currentNode = _currentNode.asStateFlow()

    private val _requestReview = MutableSharedFlow<Unit>()
    val requestReview = _requestReview.asSharedFlow()

    fun findNode(route: String) = rootNode.value.findByRoute(route)

    fun getAdjacentRoutes(node: PageNode): Pair<String?, String?> = getAdjacentSiblingRoutesUseCase(rootNode.value, node)

    fun onPrayerScreenOpened() {
        viewModelScope.launch {
            inAppReviewManager.incrementAndGetPrayerScreenVisits()
        }
    }

    suspend fun checkForReview() {
        inAppReviewManager.checkForReview()
    }

    fun onSectionScreenOpened() {
        viewModelScope.launch {
            _requestReview.emit(Unit)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun getAllPrayerNodes(): List<PageNode> =
        getPrayerNodesForCurrentTimeUseCase(
            rootNode.value,
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        )
}