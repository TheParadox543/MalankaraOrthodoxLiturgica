package com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetAdjacentSiblingRoutesUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerNodesForCurrentTimeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime

class PrayerNavViewModel(
    settingsRepository: SettingsRepository,
    private val prayerRepository: PrayerRepository,
    private val getAdjacentSiblingRoutesUseCase: GetAdjacentSiblingRoutesUseCase,
    private val getPrayerNodesForCurrentTimeUseCase: GetPrayerNodesForCurrentTimeUseCase,
) : ViewModel() {
    private val initialTree =
        runBlocking {
            prayerRepository.getPrayerNavigationTree(AppLanguage.MALAYALAM)
        }

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

    fun findNode(route: String) = rootNode.value.findByRoute(route)

    fun getAdjacentRoutes(node: PageNode): Pair<String?, String?> = getAdjacentSiblingRoutesUseCase(rootNode.value, node)

    fun getAllPrayerNodes(): List<PageNode> =
        // Avoid Kotlin 2.1-only Clock API; convert JVM local time to kotlinx-datetime.
        getPrayerNodesForCurrentTimeUseCase(
            rootNode.value,
            java.time.LocalDateTime.now().let {
                LocalDateTime(
                    year = it.year,
                    month = it.monthValue,
                    day = it.dayOfMonth,
                    hour = it.hour,
                    minute = it.minute,
                    second = it.second,
                    nanosecond = it.nano,
                )
            },
        )
}
