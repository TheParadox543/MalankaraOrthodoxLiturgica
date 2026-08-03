# iOS Navigation Design

Status: approved for planning
Date: 2026-07-25
Branch: `migrate/ios-salvage-from-windows`

## Context

The Android app uses Navigation-Compose: a single `NavHost`
(`androidApp/.../ui/navigation/NavGraph.kt`) with routes declared in a
sealed class `AppScreen` (`androidApp/.../ui/navigation/AppScreen.kt`).
All screen composables already live in KMP `commonMain` (across
`feature/prayer-kmp`, `feature/calendar-kmp`, `feature/bible-kmp`,
`feature/settings-kmp`, `feature/onboarding-kmp`) and are
navigation-agnostic — they take callback lambdas
(`onSectionNavigate: (String) -> Unit`, etc.), never a `NavController`
directly. Only `NavGraph.kt` owns routing.

iOS currently has **no navigation** — `MalankaraOrthodoxLiturgicaApp.swift`
mounts a single `PrayerView` as the whole app. `Platform.ios.kt` exposes
5 of ~15 screens as `UIViewController`-returning bridge functions
(`getHomeViewController`, `getSectionViewController`,
`getPrayerViewController`, `getIndexViewController`,
`getPrayNowViewController`); the rest need new bridge functions.

Two Android screens are **not portable** via KMP at all:
`SongScreen` (ExoPlayer/media3) and `QrScannerView` (CameraX) are
Android-only modules with no `commonMain`. These get native Swift
reimplementations that preserve the same navigation contract
(`onNavigate(route: String)` for the scanner, route-driven playback
for songs) rather than a shared Compose UI.

## Goals

- Full route parity with Android's `AppScreen` (all ~15 destinations).
- Idiomatic iOS navigation: `TabView` + per-tab `NavigationStack`,
  not a hand-rolled single-stack mimic of Android's bottom-bar
  `popBackStack` behavior.
- No widening of `shared/build.gradle.kts`'s `export()` list — new
  bridge functions stay primitive/closure-only across the Swift/Kotlin
  boundary, matching the existing pattern.
- Deep link parity: `app://liturgica/...` URIs handled on iOS the
  same way Android's `navDeepLink` entries work.
- Song playback and QR scanning get native iOS implementations behind
  the same navigation contract as their Android counterparts, not a
  KMP port (out of scope — those are separate, much larger, projects).

## Non-goals

- Porting `SongScreen`'s ExoPlayer-based UI or `QrScannerView`'s
  CameraX pipeline into KMP `commonMain`. Only their *navigation
  entry/exit contract* is mirrored.
- Changing Android's navigation code. This is iOS-only.
- Visual/design parity beyond what SwiftUI's native `TabView` /
  `NavigationStack` chrome provides — no attempt to pixel-match
  Android's custom `BottomNavBar`/`TopNavBar` Compose components.

## Route model

A `Hashable` Swift enum, `AppRoute`, mirrors `AppScreen.kt` one case
per Android route object:

```swift
enum AppRoute: Hashable {
    case home
    case onboarding
    case prayNow
    case bible
    case bibleReader
    case calendar
    case qrScanner
    case index
    case settings
    case about
    case section(route: String)
    case prayer(route: String, scroll: Int)
    case song(route: String)
    case bibleBook(bookIndex: Int)
    case bibleChapter(bookIndex: Int, chapterIndex: Int)

    static func prayer(_ route: String) -> AppRoute {
        .prayer(route: route, scroll: 0)
    }
}
```

Swift enum cases cannot declare default associated-value parameters
(unlike Kotlin's `createRoute(prayerRoute: String, scroll: Int = 0)`),
so the `scroll` default is provided via the static factory above.

`AppRoute` is a pure Swift value type — it does not cross the
Kotlin/Swift bridge and has no Kotlin counterpart. It only exists to
drive SwiftUI's `navigationDestination(for:)`.

## Navigation shell

- `TabView` with 4 tabs, matching the route set hardcoded in
  `core/ui-common`'s `BottomNavItems.kt` (`"home"`, `"prayNow"`,
  `"calendar"`, `"bible"`): Home, PrayNow, Calendar, Bible.
- Each tab owns an independent `NavigationStack` bound to its own
  path array, so push/pop history does not leak across tabs (matches
  the isolated-stack feel of modern iOS apps; Android achieves the
  same net effect differently, via `popBackStack(route, inclusive =
  true)` on every bottom-nav tap).
- A single `AppRouter: ObservableObject` — the Swift analogue of
  `NavGraph.kt` — owns:
  ```swift
  @Published var selectedTab: AppTab // .home, .prayNow, .calendar, .bible
  @Published var homePath: [AppRoute] = []
  @Published var prayNowPath: [AppRoute] = []
  @Published var calendarPath: [AppRoute] = []
  @Published var biblePath: [AppRoute] = []

  func push(_ route: AppRoute) // appends to selectedTab's path
  func popToRoot(_ tab: AppTab)
  func selectTab(_ tab: AppTab) // re-selecting the active tab clears its path
  ```
  Injected via `.environmentObject(router)` at the app root. This is
  the only place `AppRoute` values get created and pushed — exactly
  mirroring how only `NavGraph.kt`, never a screen composable, calls
  `navController.navigate(...)` on Android.
- Screen wrapper views (`HomeComposeView`, `SectionComposeView`, etc.)
  keep their existing per-screen navigation closures
  (`onSectionNavigate`, `onPrayerNavigate`, `onSongNavigate`,
  `onPrayNowNavigate`, `onIndexNavigate`, and new ones for
  Bible/Calendar/Settings/etc.) — each closure body becomes a one-line
  `router.push(.section(route: route))`-style call. These closure
  *signatures* are unchanged; only the `onScaffoldStateChanged` param
  on every bridge function (existing 5 included) is touched, per the
  Chrome section below.
- `NavigationStack`'s own `navigationDestination(for: AppRoute.self)`
  switches on the route to instantiate the matching wrapper view for
  every case, including `.settings`, `.about`, `.onboarding` (reachable
  from any tab's stack, not tab-exclusive).

## Chrome: back button, settings gear, FAB

Android's shared `Scaffold` in `NavGraph.kt` derives
top-bar/FAB visibility from `ScaffoldUiState` (commonMain type in
`core/ui-common`), which every screen reports via
`onScaffoldStateChanged`. Exporting `core:ui-common` to Swift so it
could branch on `ScaffoldUiState` directly would widen the framework's
public Objective-C surface for a single enum — not worth it given
every other bridge signature stays primitive-only.

Instead, each bridge function's existing `onScaffoldStateChanged`
callback is flattened at the Kotlin/Swift boundary:

```kotlin
onChromeStateChanged: (showBack: Boolean, showSettings: Boolean, showFab: Boolean, title: String) -> Unit
```

This replaces `onScaffoldStateChanged` on **every** bridge function,
existing 5 (`getHomeViewController` etc.) and new ones alike — the
signature change to those 5 is part of this design's Kotlin work, not
optional. A per-screen-wrapper `ObservableObject` (`ChromeState`)
republishes this as `@Published` properties; the SwiftUI wrapper view
reads it to conditionally attach `.toolbar` items (back chevron —
though `NavigationStack` already provides this natively, so `showBack`
mainly suppresses it for root/tab-landing screens — and a settings
gear) and a QR-scan FAB overlay. This reproduces Android's per-screen
chrome decisions without exporting any Kotlin type.

## Kotlin bridge additions (`Platform.ios.kt`)

New `getXViewController` functions, one per currently-unbridged
screen, following the existing pattern (primitives and closures only,
no exported types):

| Function | Wraps | New closures/params vs. existing pattern |
|---|---|---|
| `getOnboardingViewController` | `OnboardingScreen` (onboarding-kmp) | `onNavigateToHome: () -> Unit`, `requestDndPermission: () -> Unit` |
| `getBibleViewController` | `BibleScreen` (bible-kmp) | `onBibleNavigate: (Int) -> Unit` |
| `getBibleBookViewController` | `BibleBookScreen` (bible-kmp) | `bookIndex: Int`, `onBibleNavigate: (Int, Int) -> Unit` |
| `getBibleChapterViewController` | `BibleChapterScreen` (bible-kmp) | `bookIndex: Int`, `chapterIndex: Int`; `onQrDialogShow`/`routeFactory` collapsed into a single `onShareRequested: (Int, Int) -> String` returning the share text, backed by the already-KMP `qr-generation` module |
| `getBibleReaderViewController` | `BibleReadingScreen` (**calendar-kmp**, not bible-kmp — Android's own naming: `AppScreen.BibleReader` route renders a calendar-kmp screen keyed off `calendarViewModel`'s selected-reading state) | none new |
| `getCalendarViewController` | `CalendarLiturgicalSeasonScreen` (calendar-kmp) | `onPrayerNavigate: (String) -> Unit`, `onBibleNavigate: () -> Unit` |
| `getSettingsViewController` | `SettingsScreen` (settings-kmp) | `onNavigateToAbout: () -> Unit`, `requestDndPermission: () -> Unit`, `showSoundModeSetting: Boolean`; `shareService`/`ShareService` resolved from Koin inside the bridge function body (iOS `actual` already exists per DI setup), not passed from Swift |
| `getAboutViewController` | `AboutScreen` (settings-kmp) | `appVersion: String` (Swift supplies from `Bundle.main`), `onDeveloperContact: () -> Unit`, `onExternalLinkClick: (String) -> Unit` |

All new functions get `onChromeStateChanged` per the Chrome section
above, replacing each screen's raw `onScaffoldStateChanged` at the
bridge boundary.

No changes to `shared/build.gradle.kts`'s `export()` block — it stays
`:core:domain`, `:data:prayer`, `:feature:prayer-kmp`. Bridge functions
may freely use non-exported KMP types (`CalendarViewModel`,
`BibleViewModel`, `SettingsViewModel`, etc.) internally, same as
today; only the `UIViewController` return type and primitive
closure parameters need to be Swift-visible.

## Native-only screens

**Song** (`AppRoute.song(route:)`): Android's `SongScreen` depends on
`androidx.media3` (ExoPlayer), a module with no `commonMain` — not
portable. iOS gets:
- A new *metadata-only* Kotlin bridge function,
  `getSongMetadata(route: String) -> SongMetadata` (or similar plain
  data return, not a `UIViewController`), resolving the song's
  filename/title/lyrics via the same `prayerRootNode` lookup Android's
  `NavGraph.kt` uses.
- A native SwiftUI player view (`SongPlayerView.swift`) using
  `AVAudioPlayer`, built independently of Compose, wired into
  `navigationDestination(for:)` like any other case.

**QrScanner** (`AppRoute.qrScanner`): Android's `QrScannerView` depends
on CameraX, also not portable. iOS gets a native
`AVCaptureSession`-based scanner view (`QrScannerView.swift`,
Swift-side), preserving the same `onNavigate(route: String) -> Void`
contract Android's version has — a successful scan calls
`router.push(...)` (or `router.route(from:)`, see Deep Links below)
with the decoded route, re-entering the shared router identically to
how a scanned Android deep link would.

`qr-generation` (already a proper KMP module targeting iOS) needs no
native work — used as-is for the About/Settings "share via QR" flow.

## Deep links

- Register a custom URL scheme, `liturgica`, in the iOS app's
  `Info.plist` (`CFBundleURLTypes`), matching Android's
  `app://liturgica/...` custom-scheme deep links (not universal
  links — Android doesn't use `https://` deep links either, so this
  stays symmetric).
- `MalankaraOrthodoxLiturgicaApp.swift` adds
  `.onOpenURL { url in router.route(from: url) }` on the root view.
- `AppRoute` gains a failable parser,
  `init?(url: URL)`, whose `switch` mirrors every `DEEP_LINK_PATTERN`
  in `AppScreen.kt` verbatim (`home`, `bible`, `calendar`, `index`,
  `settings`, `about`, `section/{route}`, `prayer/{route}/{scroll}`,
  `bible/{bookIndex}`, `bible/{bookIndex}/{chapterIndex}`).
  `onboarding`, `prayNow`, `bibleReader`, `qrScanner`, `song` have no
  deep link on Android either, so `init?` returns `nil` for those
  paths (matches Android's `deepLink: String? = null` gap exactly,
  not an oversight).
- `AppRouter.route(from:)` parses the URL, selects the correct tab
  (or falls back to the current tab for tab-agnostic routes like
  `.settings`), and pushes the resulting `AppRoute`.

## Onboarding & Settings/About placement

- App root reads `hasCompletedOnboarding` from the existing shared
  settings-kmp datastore (same source of truth Android's
  `StartupState.Ready` check uses) and presents `OnboardingComposeView`
  via `.fullScreenCover` when `false` — no tab bar visible underneath,
  matching Android's separate `startDestination` swap. Completing
  onboarding (`onNavigateToHome`) dismisses the cover.
- Settings gear is chrome-driven (see Chrome section) and appears on
  any screen whose `showSettings` chrome flag is true — global by
  behavior, not restricted to the Home tab, matching Android's
  shared-`TopNavBar`-on-every-`Standard`-scaffold pattern. Tapping it
  pushes `.settings` onto whichever tab's stack is currently active.
- About is reachable only from Settings (`onNavigateToAbout`), same
  as Android — no direct tab or deep-link-only entry beyond that one
  call site plus the `app://liturgica/about` deep link.

## Testing / verification

- Build + run on iOS Simulator (per the existing `run` skill flow
  used earlier this session) after each bridge function is added,
  confirming no `Multiple commands produce` / crash regressions like
  the ones hit fixing the base iOS build.
- Manual pass through: tab switching resets stack; deep-link a
  `prayer/{route}/{scroll}` URL via `xcrun simctl openurl` and confirm
  it lands on the right tab + pushes the right screen; onboarding
  cover dismisses correctly and does not reappear on relaunch; QR
  scan → route parse → push works end-to-end with a code encoding an
  `app://liturgica/...` URI generated by `qr-generation`.
- No automated UI test suite exists on iOS yet (`MalankaraOrthodoxLiturgicaUITests`
  is Xcode-template boilerplate) — out of scope to build one here;
  flagged as a natural follow-up, not blocking this work.
