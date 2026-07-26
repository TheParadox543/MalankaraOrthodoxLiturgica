package com.paradox543.malankaraorthodoxliturgica.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleBookScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleChapterScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens.BibleReadingScreen
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens.CalendarLiturgicalSeasonScreen
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens.OnboardingScreen
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.IndexScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayNowScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.SectionScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.settings.screens.AboutScreen
import com.paradox543.malankaraorthodoxliturgica.feature.settings.screens.SettingsScreen
import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform.getKoin
import platform.UIKit.UIViewController

actual fun platformName(): String = "iOS"

/**
 * Every `getXViewController()` below is its own `ComposeUIViewController`, so
 * unlike Android's single NavGraph composable (which creates these once and
 * threads them down — see `NavGraph.kt`'s "Create ViewModels once ... to
 * prevent recreation glitches" comment), there is no shared Compose scope to
 * hoist into. `koinViewModel()` would resolve a brand-new instance — with a
 * brand-new async-loading `rootNode`/`selectedLanguage` — on every single
 * screen push. Resolving directly through Koin once, cached here for the
 * process lifetime, gives iOS the same "created once" guarantee Android gets
 * from NavGraph, without needing a persistent ViewModelStoreOwner.
 */
private object IOSSharedViewModels {
    val prayerViewModel: PrayerViewModel by lazy { getKoin().get() }
    val prayerNavViewModel: PrayerNavViewModel by lazy { getKoin().get() }
    val bibleViewModel: BibleViewModel by lazy { getKoin().get() }
    val calendarViewModel: CalendarViewModel by lazy { getKoin().get() }
    val settingsViewModel: SettingsViewModel by lazy { getKoin().get() }
}

/**
 * `ScaffoldUiState` is intentionally not exported to Swift (it would widen
 * the framework's public surface for a single enum). Every screen wrapper
 * flattens it to primitives here before it crosses the bridge. `showBack`
 * and `showSettings` are NOT included — on Android those are computed by
 * `NavGraph.kt` from route identity (`currentRoute != AppScreen.Home.route`
 * etc.), not from `ScaffoldUiState`, so Swift's `AppRouter` computes them the
 * same way locally.
 */
private fun ScaffoldUiState.toChromeState(): Pair<String, Boolean> =
    when (this) {
        is ScaffoldUiState.Standard -> title to showFab
        is ScaffoldUiState.PrayerReading -> title to showFab
        is ScaffoldUiState.BibleChapterReading -> title to showFab
        ScaffoldUiState.None -> "" to false
    }

@Composable
fun PrayerScreenWrapper(
    route: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit,
    onSectionNavChanged: (String?, String?, () -> Unit) -> Unit
) {
    val prayerViewModel = IOSSharedViewModels.prayerViewModel
    val prayerNavViewModel = IOSSharedViewModels.prayerNavViewModel

    val rootNode by prayerNavViewModel.rootNode.collectAsState()
    val node = rootNode.findByRoute(route)

    if (node == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        LaunchedEffect(Unit) {
            onChromeStateChanged("", false)
        }
    } else {
        PrayerScreen(
            onPrayerButtonClick = onPrayerButtonClick,
            prayerViewModel = prayerViewModel,
            prayerNavViewModel = prayerNavViewModel,
            node = node,
            onQrDialogShow = { qrRoute, scroll -> "app://liturgica/prayer/$qrRoute/$scroll" },
            routeProvider = { it },
            onScaffoldStateChanged = { state ->
                val (title, showFab) = state.toChromeState()
                onChromeStateChanged(title, showFab)
                if (state is ScaffoldUiState.PrayerReading) {
                    onSectionNavChanged(state.prevRoute, state.nextRoute, state.onShowQrDialog)
                }
            }
        )
    }
}

fun getPrayerViewController(
    route: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit,
    onSectionNavChanged: (String?, String?, () -> Unit) -> Unit
): UIViewController = ComposeUIViewController {
    PrayerScreenWrapper(route, onPrayerButtonClick, onChromeStateChanged, onSectionNavChanged)
}

@Composable
fun HomeScreenWrapper(
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onPrayNowNavigate: () -> Unit,
    onIndexNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val prayerViewModel = IOSSharedViewModels.prayerViewModel
    val prayerNavViewModel = IOSSharedViewModels.prayerNavViewModel
    val calendarViewModel = IOSSharedViewModels.calendarViewModel

    val liturgicalDay by calendarViewModel.todayLiturgicalDay.collectAsState()
    val recommendedPrayers = prayerNavViewModel.getAllPrayerNodes()
    val topPrayer = recommendedPrayers.firstOrNull()

    HomeScreen(
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        liturgicalDay = liturgicalDay,
        topRecommendedPrayer = topPrayer,
        contentPadding = PaddingValues(0.dp),
        onSectionNavigate = onSectionNavigate,
        onPrayerNavigate = onPrayerNavigate,
        onSongNavigate = onSongNavigate,
        onPrayNowNavigate = onPrayNowNavigate,
        onIndexNavigate = onIndexNavigate,
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getHomeViewController(
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onPrayNowNavigate: () -> Unit,
    onIndexNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    HomeScreenWrapper(
        onSectionNavigate,
        onPrayerNavigate,
        onSongNavigate,
        onPrayNowNavigate,
        onIndexNavigate,
        onChromeStateChanged
    )
}

@Composable
fun SectionScreenWrapper(
    route: String,
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onIndexNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val prayerViewModel = IOSSharedViewModels.prayerViewModel
    val prayerNavViewModel = IOSSharedViewModels.prayerNavViewModel
    val calendarViewModel = IOSSharedViewModels.calendarViewModel

    val rootNode by prayerNavViewModel.rootNode.collectAsState()
    val node = rootNode.findByRoute(route) ?: PageNode(route = route, parent = null)
    val liturgicalDay by calendarViewModel.todayLiturgicalDay.collectAsState()

    SectionScreen(
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        node = node,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        },
        onSectionNavigate = onSectionNavigate,
        onPrayerNavigate = onPrayerNavigate,
        onSongNavigate = onSongNavigate,
        onIndexNavigate = onIndexNavigate,
        liturgicalDay = liturgicalDay
    )
}

fun getSectionViewController(
    route: String,
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
    onIndexNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    SectionScreenWrapper(
        route,
        onSectionNavigate,
        onPrayerNavigate,
        onSongNavigate,
        onIndexNavigate,
        onChromeStateChanged
    )
}

@Composable
fun IndexScreenWrapper(
    onPrayerNavigate: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val prayerViewModel = IOSSharedViewModels.prayerViewModel
    val prayerNavViewModel = IOSSharedViewModels.prayerNavViewModel

    IndexScreen(
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        contentPadding = PaddingValues(0.dp),
        onPrayerNavigate = onPrayerNavigate,
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getIndexViewController(
    onPrayerNavigate: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    IndexScreenWrapper(onPrayerNavigate, onChromeStateChanged)
}

@Composable
fun PrayNowScreenWrapper(
    onPrayerNavigate: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val prayerViewModel = IOSSharedViewModels.prayerViewModel
    val prayerNavViewModel = IOSSharedViewModels.prayerNavViewModel

    PrayNowScreen(
        onCardClick = onPrayerNavigate,
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getPrayNowViewController(
    onPrayerNavigate: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    PrayNowScreenWrapper(onPrayerNavigate, onChromeStateChanged)
}

@Composable
fun BibleScreenWrapper(
    onBibleNavigate: (Int) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val bibleViewModel = IOSSharedViewModels.bibleViewModel

    BibleScreen(
        onBibleNavigate = onBibleNavigate,
        bibleViewModel = bibleViewModel,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getBibleViewController(
    onBibleNavigate: (Int) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    BibleScreenWrapper(onBibleNavigate, onChromeStateChanged)
}

@Composable
fun BibleBookScreenWrapper(
    bookIndex: Int,
    onBibleNavigate: (Int, Int) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val bibleViewModel = IOSSharedViewModels.bibleViewModel

    BibleBookScreen(
        onBibleNavigate = onBibleNavigate,
        bibleViewModel = bibleViewModel,
        bookIndex = bookIndex,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getBibleBookViewController(
    bookIndex: Int,
    onBibleNavigate: (Int, Int) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    BibleBookScreenWrapper(bookIndex, onBibleNavigate, onChromeStateChanged)
}

@Composable
fun BibleChapterScreenWrapper(
    bookIndex: Int,
    chapterIndex: Int,
    onChromeStateChanged: (String, Boolean) -> Unit,
    onSectionNavChanged: (String?, String?, () -> Unit) -> Unit
) {
    val bibleViewModel = IOSSharedViewModels.bibleViewModel

    BibleChapterScreen(
        bibleViewModel = bibleViewModel,
        bookIndex = bookIndex,
        chapterIndex = chapterIndex,
        contentPadding = PaddingValues(0.dp),
        onQrDialogShow = { book, chapter -> "app://liturgica/bible/$book/$chapter" },
        routeFactory = { ref -> "bible/${ref.bookIndex}/${ref.chapterIndex}" },
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
            if (state is ScaffoldUiState.BibleChapterReading) {
                onSectionNavChanged(
                    state.prevRoute?.let { "${it.bookIndex}/${it.chapterIndex}" },
                    state.nextRoute?.let { "${it.bookIndex}/${it.chapterIndex}" },
                    state.onShowQrDialog
                )
            }
        }
    )
}

fun getBibleChapterViewController(
    bookIndex: Int,
    chapterIndex: Int,
    onChromeStateChanged: (String, Boolean) -> Unit,
    onSectionNavChanged: (String?, String?, () -> Unit) -> Unit
): UIViewController = ComposeUIViewController {
    BibleChapterScreenWrapper(bookIndex, chapterIndex, onChromeStateChanged, onSectionNavChanged)
}

@Composable
fun CalendarScreenWrapper(
    onPrayerNavigate: (String) -> Unit,
    onBibleNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val calendarViewModel = IOSSharedViewModels.calendarViewModel

    CalendarLiturgicalSeasonScreen(
        calendarViewModel = calendarViewModel,
        contentPadding = PaddingValues(0.dp),
        onPrayerNavigate = onPrayerNavigate,
        onBibleNavigate = onBibleNavigate,
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getCalendarViewController(
    onPrayerNavigate: (String) -> Unit,
    onBibleNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    CalendarScreenWrapper(onPrayerNavigate, onBibleNavigate, onChromeStateChanged)
}

@Composable
fun BibleReaderScreenWrapper(
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val calendarViewModel = IOSSharedViewModels.calendarViewModel

    BibleReadingScreen(
        calendarViewModel = calendarViewModel,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getBibleReaderViewController(
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    BibleReaderScreenWrapper(onChromeStateChanged)
}

@Composable
fun SettingsScreenWrapper(
    onNavigateToAbout: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val settingsViewModel = IOSSharedViewModels.settingsViewModel
    val shareService: ShareService = getKoin().get()

    SettingsScreen(
        onNavigateToAbout = onNavigateToAbout,
        // iOS has no notification-interruption API equivalent to Android's DND
        // access request; IOSSoundModeCapability.isAvailable is already false,
        // so this button is hidden below and this callback is unreachable.
        requestDndPermission = { },
        settingsViewModel = settingsViewModel,
        shareService = shareService,
        showSoundModeSetting = false,
        contentPadding = PaddingValues(0.dp),
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getSettingsViewController(
    onNavigateToAbout: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    SettingsScreenWrapper(onNavigateToAbout, onChromeStateChanged)
}

@Composable
fun AboutScreenWrapper(
    appVersion: String,
    onDeveloperContact: () -> Unit,
    onExternalLinkClick: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    AboutScreen(
        contentPadding = PaddingValues(0.dp),
        appVersion = appVersion,
        onDeveloperContact = onDeveloperContact,
        onExternalLinkClick = onExternalLinkClick,
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getAboutViewController(
    appVersion: String,
    onDeveloperContact: () -> Unit,
    onExternalLinkClick: (String) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    AboutScreenWrapper(appVersion, onDeveloperContact, onExternalLinkClick, onChromeStateChanged)
}

@Composable
fun OnboardingScreenWrapper(
    onNavigateToHome: () -> Unit
) {
    val onboardingViewModel: OnboardingViewModel = koinViewModel()

    OnboardingScreen(
        onboardingViewModel = onboardingViewModel,
        contentPadding = PaddingValues(0.dp),
        onNavigateToHome = onNavigateToHome,
        requestDndPermission = { },
        onScaffoldStateChanged = { }
    )
}

fun getOnboardingViewController(
    onNavigateToHome: () -> Unit
): UIViewController = ComposeUIViewController {
    OnboardingScreenWrapper(onNavigateToHome)
}

/**
 * Snapshot-reads whether onboarding is complete, matching the intent of
 * Android's `onboardingCompleted` check in `NavGraph.kt` (which derives it
 * from `onboardingStage > 0`) but reads `SettingsRepository.onboardingCompleted`
 * directly since that flow already exists for exactly this purpose.
 */
fun getOnboardingCompleted(onResult: (Boolean) -> Unit) {
    val settingsRepository: SettingsRepository = getKoin().get()
    CoroutineScope(Dispatchers.Main).launch {
        onResult(settingsRepository.onboardingCompleted.first())
    }
}

data class SongMetadata(
    val filename: String
)

fun getSongMetadata(route: String): SongMetadata? {
    val node = IOSSharedViewModels.prayerNavViewModel.rootNode.value.findByRoute(route) ?: return null
    val filename = node.filename ?: return null
    return SongMetadata(filename = filename)
}
