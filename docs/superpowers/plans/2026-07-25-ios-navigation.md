# iOS Navigation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a full-parity iOS navigation shell (`TabView` + per-tab `NavigationStack`) mirroring Android's Navigation-Compose `AppScreen` routes, wiring all KMP-shared screens through new Kotlin bridge functions and giving Song/QrScanner native Swift implementations.

**Architecture:** A Swift `AppRoute` enum mirrors `AppScreen.kt`. A single `AppRouter: ObservableObject` (the Swift analogue of `NavGraph.kt`) owns per-tab navigation paths and is the only place `AppRoute` values get pushed. Existing KMP screen composables stay navigation-agnostic (closures in, `UIViewController` out) — Kotlin bridge functions in `Platform.ios.kt` get extended, not restructured.

**Tech Stack:** SwiftUI (`TabView`, `NavigationStack`, Swift Testing for unit tests), Kotlin Multiplatform (Koin DI, Compose Multiplatform via `ComposeUIViewController`), AVFoundation (native QR scanner), AVFAudio (native song player).

## Global Constraints

- No changes to `shared/build.gradle.kts`'s `export()` list — stays `:core:domain`, `:data:prayer`, `:feature:prayer-kmp`. New bridge functions use only `UIViewController` returns and primitive/closure parameters.
- No changes to Android code (`androidApp/`) — iOS-only work.
- Every new/modified Kotlin bridge function lives in `shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt`.
- New Swift files go directly in `iosApp/MalankaraOrthodoxLiturgica/` — this directory is an Xcode 16 file-system-synchronized group (`PBXFileSystemSynchronizedRootGroup`), so new files there are automatically picked up by the build; no `.pbxproj` edits needed for them.
- Build verification command after every Kotlin or Swift change:
  ```bash
  cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
  xcodebuild -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj \
    -scheme MalankaraOrthodoxLiturgica -configuration Debug \
    -destination "platform=iOS Simulator,name=iPhone 17" build 2>&1 | tail -30
  ```
  This transitively runs `./gradlew :shared:embedAndSignAppleFrameworkForXcode` via the project's existing Run Script build phase, so Kotlin changes are picked up automatically.
- Deploy/screenshot verification loop (reuse after any task marked "build+run verify"):
  ```bash
  xcrun simctl boot 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 2>/dev/null; open -a Simulator
  APP_PATH="/Users/praneethm/Library/Developer/Xcode/DerivedData/MalankaraOrthodoxLiturgica-anvrclbqzojppzhkdynjqrrsmqxy/Build/Products/Debug-iphonesimulator/MalankaraOrthodoxLiturgica.app"
  xcrun simctl install 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 "$APP_PATH"
  xcrun simctl launch 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 com.paradox543.MalankaraOrthodoxLiturgica
  sleep 3
  xcrun simctl io 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 screenshot /tmp/ios_verify.png
  ```
  (If that simulator UDID is gone, substitute any booted `iPhone` sim from `xcrun simctl list devices`.)
- Swift unit tests use the **Swift Testing** framework (`import Testing`, `@Test`, `#expect`) — matching the existing `MalankaraOrthodoxLiturgicaTests.swift` stub, not XCTest.
- Run Swift unit tests with:
  ```bash
  xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj \
    -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" \
    -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40
  ```

## Deviations from the approved spec

Discovered while reading real source during planning — flagging explicitly per the plan's own accuracy requirements, not silently changing scope:

1. **Chrome flattening is `(title: String, showFab: Boolean)`, not `(showBack, showSettings, showFab, title)`.** Reading `ScaffoldUiState.kt` and `NavGraph.kt:181-182` shows `showBack`/`showSettings` are **not** part of `ScaffoldUiState` — Android computes them itself as `currentRoute != AppScreen.Home.route` / `currentRoute != AppScreen.Settings.route`. Swift's `AppRouter` already knows the current route and stack depth, so it computes these the same way, locally, with zero Kotlin involvement. Only `title` and `showFab` genuinely originate from screen-reported `ScaffoldUiState`.
2. **`PrayerReading`/`BibleChapterReading`'s prev/next swipe navigation (`SectionNavBar`) is out of scope for this plan.** Android's `ScaffoldUiState.PrayerReading`/`BibleChapterReading` carry `prevRoute`/`nextRoute`/`routeProvider`/`onShowQrDialog`/`nestedScrollConnection` for in-reading swipe-to-next-prayer/chapter — none of these are bridge-safe primitives, and it's a refinement of an already-working reading screen, not core routing. Bridge functions pass `onQrDialogShow = { _, _ -> "" }` for this (already-established precedent — the existing `PrayerScreenWrapper` in `Platform.ios.kt:46` already stubs this exact param the same way).
3. **Settings/Onboarding need three new iOS-side Koin registrations that don't exist yet**: `ShareService` has zero iOS implementation anywhere (Android-only, lives in `androidApp/services/ShareServiceImpl.kt`); `SoundModeCapability` and `AppInfoProvider` have iOS classes (`IOSSoundModeCapability`, `IOSAppInfoProvider`) that exist but aren't registered in any Koin module. `AnalyticsService` **is** already registered with a no-op iOS implementation in `iosPlatformModule` — no work needed there.
4. **`AppScreen.BibleReader` renders `BibleReadingScreen` from `feature/calendar-kmp`, not a `BibleReaderScreen`.** This is Android's own naming inconsistency (confirmed at `NavGraph.kt:642-647`, which passes `calendarViewModel` to it), not a typo in this plan.

---

### Task 1: `AppRoute` — the Swift route model

**Files:**
- Create: `iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRoute.swift`
- Test: `iosApp/MalankaraOrthodoxLiturgicaTests/AppRouteTests.swift`

**Interfaces:**
- Produces: `enum AppRoute: Hashable` with cases `.home, .onboarding, .prayNow, .bible, .bibleReader, .calendar, .qrScanner, .index, .settings, .about, .section(route: String), .prayer(route: String, scroll: Int), .song(route: String), .bibleBook(bookIndex: Int), .bibleChapter(bookIndex: Int, chapterIndex: Int)`, plus `static func prayer(_ route: String) -> AppRoute`.

- [ ] **Step 1: Write the failing test**

```swift
// iosApp/MalankaraOrthodoxLiturgicaTests/AppRouteTests.swift
import Testing
@testable import MalankaraOrthodoxLiturgica

struct AppRouteTests {
    @Test func prayerConvenienceFactoryDefaultsScrollToZero() async throws {
        let route = AppRoute.prayer("morning")
        #expect(route == AppRoute.prayer(route: "morning", scroll: 0))
    }

    @Test func distinctCasesAreNotEqual() async throws {
        #expect(AppRoute.home != AppRoute.bible)
        #expect(AppRoute.section(route: "a") != AppRoute.section(route: "b"))
    }

    @Test func sameAssociatedValuesAreEqual() async throws {
        #expect(AppRoute.bibleChapter(bookIndex: 1, chapterIndex: 2) == AppRoute.bibleChapter(bookIndex: 1, chapterIndex: 2))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: FAIL — `cannot find 'AppRoute' in scope`.

- [ ] **Step 3: Write the implementation**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRoute.swift
import Foundation

/// Mirrors `androidApp/.../ui/navigation/AppScreen.kt` one case per Android route object.
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

    /// Swift enum cases can't declare default associated-value parameters
    /// (unlike Kotlin's `createRoute(prayerRoute: String, scroll: Int = 0)`).
    static func prayer(_ route: String) -> AppRoute {
        .prayer(route: route, scroll: 0)
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: PASS (3 tests).

- [ ] **Step 5: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRoute.swift iosApp/MalankaraOrthodoxLiturgicaTests/AppRouteTests.swift
git commit -m "feat(ios): add AppRoute enum mirroring Android's AppScreen routes"
```

---

### Task 2: `AppRouter` — per-tab navigation state

**Files:**
- Create: `iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRouter.swift`
- Test: `iosApp/MalankaraOrthodoxLiturgicaTests/AppRouterTests.swift`

**Interfaces:**
- Consumes: `AppRoute` (Task 1).
- Produces:
  ```swift
  enum AppTab: String, CaseIterable, Hashable { case home, prayNow, calendar, bible }

  final class AppRouter: ObservableObject {
      @Published var selectedTab: AppTab
      @Published var homePath: [AppRoute]
      @Published var prayNowPath: [AppRoute]
      @Published var calendarPath: [AppRoute]
      @Published var biblePath: [AppRoute]
      @Published var showOnboarding: Bool

      func push(_ route: AppRoute)
      func popToRoot(_ tab: AppTab)
      func pathBinding(for tab: AppTab) -> Binding<[AppRoute]>
      func handleTabReselect(_ tappedTab: AppTab)
  }
  ```

- [ ] **Step 1: Write the failing test**

```swift
// iosApp/MalankaraOrthodoxLiturgicaTests/AppRouterTests.swift
import Testing
@testable import MalankaraOrthodoxLiturgica

@MainActor
struct AppRouterTests {
    @Test func pushAppendsToSelectedTabsPath() async throws {
        let router = AppRouter()
        router.selectedTab = .home
        router.push(.section(route: "morning"))
        #expect(router.homePath == [.section(route: "morning")])
        #expect(router.prayNowPath.isEmpty)
    }

    @Test func pushOnDifferentTabUsesThatTabsPath() async throws {
        let router = AppRouter()
        router.selectedTab = .bible
        router.push(.bibleBook(bookIndex: 0))
        #expect(router.biblePath == [.bibleBook(bookIndex: 0)])
    }

    @Test func popToRootClearsOnlyThatTabsPath() async throws {
        let router = AppRouter()
        router.homePath = [.section(route: "a")]
        router.calendarPath = [.bibleReader]
        router.popToRoot(.home)
        #expect(router.homePath.isEmpty)
        #expect(router.calendarPath == [.bibleReader])
    }

    @Test func reselectingActiveTabPopsItToRoot() async throws {
        let router = AppRouter()
        router.selectedTab = .prayNow
        router.prayNowPath = [.prayer(route: "morning", scroll: 0)]
        router.handleTabReselect(.prayNow)
        #expect(router.prayNowPath.isEmpty)
        #expect(router.selectedTab == .prayNow)
    }

    @Test func reselectingDifferentTabJustSwitches() async throws {
        let router = AppRouter()
        router.selectedTab = .home
        router.calendarPath = [.bibleReader]
        router.handleTabReselect(.calendar)
        #expect(router.selectedTab == .calendar)
        #expect(router.calendarPath == [.bibleReader])
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: FAIL — `cannot find 'AppRouter' in scope`.

- [ ] **Step 3: Write the implementation**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRouter.swift
import SwiftUI

enum AppTab: String, CaseIterable, Hashable {
    case home, prayNow, calendar, bible

    var title: String {
        switch self {
        case .home: return "Home"
        case .prayNow: return "Pray Now"
        case .calendar: return "Calendar"
        case .bible: return "Bible"
        }
    }

    var systemImage: String {
        switch self {
        case .home: return "house"
        case .prayNow: return "hands.sparkles"
        case .calendar: return "calendar"
        case .bible: return "book"
        }
    }
}

/// The Swift analogue of Android's `NavGraph.kt` — the only place `AppRoute`
/// values get created and pushed. Screens stay navigation-agnostic; they call
/// closures that end up calling `push(_:)` here, mirroring how only
/// `NavGraph.kt`, never a screen composable, calls `navController.navigate(...)`.
@MainActor
final class AppRouter: ObservableObject {
    @Published var selectedTab: AppTab = .home
    @Published var homePath: [AppRoute] = []
    @Published var prayNowPath: [AppRoute] = []
    @Published var calendarPath: [AppRoute] = []
    @Published var biblePath: [AppRoute] = []
    @Published var showOnboarding: Bool = false

    func push(_ route: AppRoute) {
        switch selectedTab {
        case .home: homePath.append(route)
        case .prayNow: prayNowPath.append(route)
        case .calendar: calendarPath.append(route)
        case .bible: biblePath.append(route)
        }
    }

    func popToRoot(_ tab: AppTab) {
        switch tab {
        case .home: homePath.removeAll()
        case .prayNow: prayNowPath.removeAll()
        case .calendar: calendarPath.removeAll()
        case .bible: biblePath.removeAll()
        }
    }

    func pathBinding(for tab: AppTab) -> Binding<[AppRoute]> {
        switch tab {
        case .home:
            return Binding(get: { self.homePath }, set: { self.homePath = $0 })
        case .prayNow:
            return Binding(get: { self.prayNowPath }, set: { self.prayNowPath = $0 })
        case .calendar:
            return Binding(get: { self.calendarPath }, set: { self.calendarPath = $0 })
        case .bible:
            return Binding(get: { self.biblePath }, set: { self.biblePath = $0 })
        }
    }

    /// Mirrors Android's bottom-nav tap behavior
    /// (`navController.navigate(route) { popBackStack(route, inclusive = true) }`):
    /// tapping the already-active tab clears its stack instead of doing nothing.
    func handleTabReselect(_ tappedTab: AppTab) {
        if tappedTab == selectedTab {
            popToRoot(tappedTab)
        } else {
            selectedTab = tappedTab
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: PASS (5 tests).

- [ ] **Step 5: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRouter.swift iosApp/MalankaraOrthodoxLiturgicaTests/AppRouterTests.swift
git commit -m "feat(ios): add AppRouter owning per-tab navigation paths"
```

---

### Task 3: Deep link URL scheme + `AppRoute` URL parser

**Files:**
- Create: `iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRoute+DeepLink.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica-Info.plist`
- Test: `iosApp/MalankaraOrthodoxLiturgicaTests/AppRouteDeepLinkTests.swift`

**Interfaces:**
- Consumes: `AppRoute` (Task 1).
- Produces: `extension AppRoute { init?(url: URL) }`. Consumed directly by `MalankaraOrthodoxLiturgicaApp.swift`'s `.onOpenURL` in Task 5 (`AppRoute(url: url)`) and by `AppRouter.route(fromScannedString:)` in Task 14 — no separate router-level wrapper method is needed.

- [ ] **Step 1: Write the failing test**

```swift
// iosApp/MalankaraOrthodoxLiturgicaTests/AppRouteDeepLinkTests.swift
import Testing
import Foundation
@testable import MalankaraOrthodoxLiturgica

struct AppRouteDeepLinkTests {
    @Test func parsesSimpleRoutes() async throws {
        #expect(AppRoute(url: URL(string: "liturgica://home")!) == .home)
        #expect(AppRoute(url: URL(string: "liturgica://bible")!) == .bible)
        #expect(AppRoute(url: URL(string: "liturgica://calendar")!) == .calendar)
        #expect(AppRoute(url: URL(string: "liturgica://index")!) == .index)
        #expect(AppRoute(url: URL(string: "liturgica://settings")!) == .settings)
        #expect(AppRoute(url: URL(string: "liturgica://about")!) == .about)
    }

    @Test func parsesSectionRoute() async throws {
        #expect(AppRoute(url: URL(string: "liturgica://section/morningPrayer")!) == .section(route: "morningPrayer"))
    }

    @Test func parsesPrayerRouteWithScroll() async throws {
        #expect(AppRoute(url: URL(string: "liturgica://prayer/morning/40")!) == .prayer(route: "morning", scroll: 40))
    }

    @Test func parsesBibleBookVsBibleChapterByComponentCount() async throws {
        #expect(AppRoute(url: URL(string: "liturgica://bible/1")!) == .bibleBook(bookIndex: 1))
        #expect(AppRoute(url: URL(string: "liturgica://bible/1/2")!) == .bibleChapter(bookIndex: 1, chapterIndex: 2))
    }

    @Test func routesWithNoAndroidDeepLinkReturnNil() async throws {
        // onboarding, prayNow, bibleReader, qrScanner, song have no deep link
        // on Android either (AppScreen.kt constructor defaults deepLink to null).
        #expect(AppRoute(url: URL(string: "liturgica://onboarding")!) == nil)
        #expect(AppRoute(url: URL(string: "liturgica://prayNow")!) == nil)
        #expect(AppRoute(url: URL(string: "liturgica://song/aRoute")!) == nil)
    }

    @Test func unknownHostReturnsNil() async throws {
        #expect(AppRoute(url: URL(string: "liturgica://nonsense/path")!) == nil)
    }

    @Test func malformedNumericSegmentsReturnNil() async throws {
        #expect(AppRoute(url: URL(string: "liturgica://prayer/morning/notANumber")!) == nil)
        #expect(AppRoute(url: URL(string: "liturgica://bible/notANumber")!) == nil)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: FAIL — `value of type 'AppRoute' has no member 'init(url:)'`.

- [ ] **Step 3: Write the implementation**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRoute+DeepLink.swift
import Foundation

/// Parses `liturgica://...` URLs into an `AppRoute`, mirroring every
/// `DEEP_LINK_PATTERN` in `AppScreen.kt` verbatim. Android's custom scheme is
/// `app://liturgica/...`; iOS registers `liturgica://...` directly (see
/// `iosApp/MalankaraOrthodoxLiturgica-Info.plist`'s `CFBundleURLTypes`) since
/// a custom URL scheme's "host" plays the same role Android's URI authority
/// does — there is no functional difference, just where the scheme boundary
/// falls.
extension AppRoute {
    init?(url: URL) {
        guard let host = url.host, !host.isEmpty else { return nil }
        let pathComponents = url.pathComponents.filter { $0 != "/" }

        switch host {
        case "home" where pathComponents.isEmpty:
            self = .home
        case "bible" where pathComponents.isEmpty:
            self = .bible
        case "calendar" where pathComponents.isEmpty:
            self = .calendar
        case "index" where pathComponents.isEmpty:
            self = .index
        case "settings" where pathComponents.isEmpty:
            self = .settings
        case "about" where pathComponents.isEmpty:
            self = .about
        case "section":
            guard pathComponents.count == 1 else { return nil }
            self = .section(route: pathComponents[0])
        case "prayer":
            guard pathComponents.count == 2, let scroll = Int(pathComponents[1]) else { return nil }
            self = .prayer(route: pathComponents[0], scroll: scroll)
        case "bible":
            guard let bookIndex = Int(pathComponents[0]) else { return nil }
            if pathComponents.count == 1 {
                self = .bibleBook(bookIndex: bookIndex)
            } else if pathComponents.count == 2, let chapterIndex = Int(pathComponents[1]) {
                self = .bibleChapter(bookIndex: bookIndex, chapterIndex: chapterIndex)
            } else {
                return nil
            }
        default:
            // onboarding, prayNow, bibleReader, qrScanner, song: no Android
            // deep link exists for these either (AppScreen.kt's `deepLink`
            // constructor param defaults to null for all five).
            return nil
        }
    }
}
```

- [ ] **Step 4: Register the custom URL scheme**

Read the current `iosApp/MalankaraOrthodoxLiturgica-Info.plist`, then edit it to add `CFBundleURLTypes`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>CADisableMinimumFrameDurationOnPhone</key>
	<true/>
	<key>CFBundleURLTypes</key>
	<array>
		<dict>
			<key>CFBundleURLName</key>
			<string>com.paradox543.MalankaraOrthodoxLiturgica</string>
			<key>CFBundleURLSchemes</key>
			<array>
				<string>liturgica</string>
			</array>
		</dict>
	</array>
</dict>
</plist>
```

- [ ] **Step 5: Run test to verify it passes**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: PASS (7 tests).

- [ ] **Step 6: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRoute+DeepLink.swift iosApp/MalankaraOrthodoxLiturgica-Info.plist iosApp/MalankaraOrthodoxLiturgicaTests/AppRouteDeepLinkTests.swift
git commit -m "feat(ios): parse liturgica:// deep links into AppRoute, register URL scheme"
```

---

### Task 4: Flatten chrome callback on the 5 existing Kotlin bridge functions

**Files:**
- Modify: `shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt`

**Interfaces:**
- Consumes: `ScaffoldUiState` (`core/ui-common/src/commonMain/.../core/ui/scaffold/ScaffoldUiState.kt` — sealed class with `Standard(title, showBottomBar, showFab)`, `PrayerReading(title, ..., showFab)`, `BibleChapterReading(title, ..., showFab)`, `None`).
- Produces: every `getXViewController` function gains a trailing `onChromeStateChanged: (title: String, showFab: Boolean) -> Unit` parameter, replacing the hardcoded `onScaffoldStateChanged = { }` no-op that exists today.

This task has no automated test — it's a pure signature/wiring change to Kotlin code that only Swift consumes, and Swift consumers don't exist until Task 5. Verification is the build in Step 2.

- [ ] **Step 1: Rewrite `Platform.ios.kt` with the flattened chrome callback**

Read the current file first (`shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt`), then replace its full contents with:

```kotlin
package com.paradox543.malankaraorthodoxliturgica.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.IndexScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayNowScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.SectionScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import org.koin.mp.KoinPlatform.getKoin
import platform.UIKit.UIViewController

actual fun platformName(): String = "iOS"

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
    fileName: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()

    val node = remember(fileName) {
        PageNode(
            route = fileName.substringBeforeLast("."),
            filename = fileName,
            parent = null
        )
    }

    PrayerScreen(
        onPrayerButtonClick = onPrayerButtonClick,
        prayerViewModel = prayerViewModel,
        prayerNavViewModel = prayerNavViewModel,
        node = node,
        onQrDialogShow = { _, _ -> "" },
        routeProvider = { it },
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getPrayerViewController(
    fileName: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    PrayerScreenWrapper(fileName, onPrayerButtonClick, onChromeStateChanged)
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
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()
    val calendarViewModel: CalendarViewModel = koin.get()

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
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()
    val calendarViewModel: CalendarViewModel = koin.get()

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
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()

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
    val koin = getKoin()
    val prayerViewModel: PrayerViewModel = koin.get()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()

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
```

- [ ] **Step 2: Build to verify Kotlin compiles**

Run:
```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
./gradlew :shared:compileKotlinIosSimulatorArm64 2>&1 | tail -40
```
Expected: `BUILD SUCCESSFUL`. (The iOS app build itself will fail at this point — Swift call sites in `HomeComposeView.swift` etc. don't pass the new `onChromeStateChanged` argument yet. That's fixed in Task 5.)

- [ ] **Step 3: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt
git commit -m "feat(ios): flatten ScaffoldUiState to (title, showFab) across existing bridge functions"
```

---

### Task 5: Tab shell — wire the 5 existing screens into `TabView` + `NavigationStack`

**Files:**
- Create: `iosApp/MalankaraOrthodoxLiturgica/Navigation/RootTabView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/Navigation/ChromeState.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/HomeTabRootView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/PrayNowTabRootView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/HomeComposeView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/SectionComposeView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/ComposeView.swift` (Prayer)
- Modify: `iosApp/MalankaraOrthodoxLiturgica/IndexComposeView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/PrayNowComposeView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/PrayerView.swift` (delete its old role as app root — repurposed or removed, see Step 6)
- Modify: `iosApp/MalankaraOrthodoxLiturgica/MalankaraOrthodoxLiturgicaApp.swift`

**Interfaces:**
- Consumes: `AppRoute`/`AppRouter`/`AppTab` (Tasks 1–2), `AppRoute(url:)` (Task 3), the 5 flattened bridge functions (Task 4).
- Produces: `RootTabView` — the new app root, mounted from `MalankaraOrthodoxLiturgicaApp.swift`. `ChromeState: ObservableObject` — one instance per pushed screen, `@Published var title: String`, `@Published var showFab: Bool`.

- [ ] **Step 1: `ChromeState` — per-screen chrome publisher**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Navigation/ChromeState.swift
import Foundation

/// Republishes a Kotlin bridge function's flattened `onChromeStateChanged`
/// callback as `@Published` state a SwiftUI wrapper view can read to drive
/// its toolbar/FAB — the Swift-side half of the Chrome design in
/// `docs/superpowers/specs/2026-07-25-ios-navigation-design.md`.
final class ChromeState: ObservableObject {
    @Published var title: String = ""
    @Published var showFab: Bool = false

    func update(title: String, showFab: Bool) {
        self.title = title
        self.showFab = showFab
    }
}
```

- [ ] **Step 2: Update the 5 `UIViewControllerRepresentable` wrappers to thread `ChromeState` through**

Read each file first, then replace its contents.

```swift
// iosApp/MalankaraOrthodoxLiturgica/HomeComposeView.swift
import SwiftUI
import sharedKit

struct HomeComposeView: UIViewControllerRepresentable {
    let onSectionNavigate: (String) -> Void
    let onPrayerNavigate: (String) -> Void
    let onSongNavigate: (String) -> Void
    let onPrayNowNavigate: () -> Void
    let onIndexNavigate: () -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getHomeViewController(
            onSectionNavigate: onSectionNavigate,
            onPrayerNavigate: onPrayerNavigate,
            onSongNavigate: onSongNavigate,
            onPrayNowNavigate: onPrayNowNavigate,
            onIndexNavigate: onIndexNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/SectionComposeView.swift
import SwiftUI
import sharedKit

struct SectionComposeView: UIViewControllerRepresentable {
    let route: String
    let onSectionNavigate: (String) -> Void
    let onPrayerNavigate: (String) -> Void
    let onSongNavigate: (String) -> Void
    let onIndexNavigate: () -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getSectionViewController(
            route: route,
            onSectionNavigate: onSectionNavigate,
            onPrayerNavigate: onPrayerNavigate,
            onSongNavigate: onSongNavigate,
            onIndexNavigate: onIndexNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/ComposeView.swift  (wraps getPrayerViewController)
import SwiftUI
import sharedKit

struct ComposeView: UIViewControllerRepresentable {
    let fileName: String
    let onPrayerButtonClick: (String, Bool) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getPrayerViewController(
            fileName: fileName,
            onPrayerButtonClick: { link, replace in
                onPrayerButtonClick(link, replace.boolValue)
            },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/IndexComposeView.swift
import SwiftUI
import sharedKit

struct IndexComposeView: UIViewControllerRepresentable {
    let onPrayerNavigate: (String) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getIndexViewController(
            onPrayerNavigate: onPrayerNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/PrayNowComposeView.swift
import SwiftUI
import sharedKit

struct PrayNowComposeView: UIViewControllerRepresentable {
    let onPrayerNavigate: (String) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getPrayNowViewController(
            onPrayerNavigate: onPrayerNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

- [ ] **Step 3: Tab-root wrapper screens (own their `ChromeState`, host the navigation-triggering closures)**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/HomeTabRootView.swift
import SwiftUI

struct HomeTabRootView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        HomeComposeView(
            onSectionNavigate: { router.push(.section(route: $0)) },
            onPrayerNavigate: { router.push(.prayer($0)) },
            onSongNavigate: { router.push(.song(route: $0)) },
            onPrayNowNavigate: { router.selectedTab = .prayNow },
            onIndexNavigate: { router.push(.index) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/PrayNowTabRootView.swift
import SwiftUI

struct PrayNowTabRootView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        PrayNowComposeView(
            onPrayerNavigate: { router.push(.prayer($0)) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

- [ ] **Step 4: `RootTabView` — the `TabView` + per-tab `NavigationStack` shell**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Navigation/RootTabView.swift
import SwiftUI

struct RootTabView: View {
    @EnvironmentObject var router: AppRouter

    var body: some View {
        TabView(selection: Binding(
            get: { router.selectedTab },
            set: { router.handleTabReselect($0) }
        )) {
            ForEach(AppTab.allCases, id: \.self) { tab in
                NavigationStack(path: router.pathBinding(for: tab)) {
                    tabRoot(for: tab)
                        .navigationDestination(for: AppRoute.self) { route in
                            destination(for: route)
                        }
                }
                .tabItem { Label(tab.title, systemImage: tab.systemImage) }
                .tag(tab)
            }
        }
        .fullScreenCover(isPresented: $router.showOnboarding) {
            OnboardingComposeView(onNavigateToHome: { router.showOnboarding = false })
        }
    }

    @ViewBuilder
    private func tabRoot(for tab: AppTab) -> some View {
        switch tab {
        case .home: HomeTabRootView()
        case .prayNow: PrayNowTabRootView()
        case .calendar: CalendarTabRootView()
        case .bible: BibleTabRootView()
        }
    }

    @ViewBuilder
    private func destination(for route: AppRoute) -> some View {
        switch route {
        case .home: HomeTabRootView()
        case .prayNow: PrayNowTabRootView()
        case .calendar: CalendarTabRootView()
        case .bible: BibleTabRootView()
        case .onboarding:
            EmptyView() // reached only via .fullScreenCover, never pushed
        case .bibleReader: BibleReaderPushedView()
        case .qrScanner: QrScannerPushedView()
        case .index: IndexPushedView()
        case .settings: SettingsPushedView()
        case .about: AboutPushedView()
        case .section(let sectionRoute): SectionPushedView(route: sectionRoute)
        case .prayer(let prayerRoute, let scroll): PrayerPushedView(route: prayerRoute, scroll: scroll)
        case .song(let songRoute): SongPushedView(route: songRoute)
        case .bibleBook(let bookIndex): BibleBookPushedView(bookIndex: bookIndex)
        case .bibleChapter(let bookIndex, let chapterIndex):
            BibleChapterPushedView(bookIndex: bookIndex, chapterIndex: chapterIndex)
        }
    }
}
```

`CalendarTabRootView`, `BibleTabRootView`, `BibleReaderPushedView`, `IndexPushedView`, `SettingsPushedView`, `AboutPushedView`, `BibleBookPushedView`, `BibleChapterPushedView`, `OnboardingComposeView` are created in Tasks 8/11. `QrScannerPushedView`/`SongPushedView` are created in Tasks 13/14. `SectionPushedView`/`PrayerPushedView` are created in Step 5 below (they exist today as `HomeComposeView`-style structs but weren't push-destinations).

- [ ] **Step 5: `SectionPushedView`/`PrayerPushedView`/`IndexPushedView` — thin views wrapping the now-push-capable Compose screens**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/SectionPushedView.swift
import SwiftUI

struct SectionPushedView: View {
    let route: String
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        SectionComposeView(
            route: route,
            onSectionNavigate: { router.push(.section(route: $0)) },
            onPrayerNavigate: { router.push(.prayer($0)) },
            onSongNavigate: { router.push(.song(route: $0)) },
            onIndexNavigate: { router.push(.index) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/PrayerPushedView.swift
import SwiftUI

struct PrayerPushedView: View {
    let route: String
    let scroll: Int
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        ComposeView(
            fileName: route,
            onPrayerButtonClick: { _, _ in },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/IndexPushedView.swift
import SwiftUI

struct IndexPushedView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        IndexComposeView(
            onPrayerNavigate: { router.push(.prayer($0)) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

`route`/`scroll` on `PrayerPushedView` map onto `ComposeView`'s existing `fileName` param — the Prayer bridge takes a filename, and Android's `Prayer.createRoute(prayerRoute, scroll)` uses the same `route` string as the file lookup key (see `PrayerScreenWrapper` in `Platform.ios.kt`, which does `fileName.substringBeforeLast(".")`). `scroll` isn't consumed by the current `getPrayerViewController` signature — Android threads it through `PrayerScreen`'s own scroll-restoration state, which isn't part of this plan's bridge surface; carried on `PrayerPushedView` for forward-compatibility with a future bridge parameter, unused today. Delete the now-obsolete `iosApp/MalankaraOrthodoxLiturgica/ContentView.swift` (unused Xcode template leftover, confirmed not referenced anywhere) as part of this step — read it first, then remove the file.

- [ ] **Step 6: Wire `RootTabView` as the app root, remove `PrayerView`'s old role**

Read `iosApp/MalankaraOrthodoxLiturgica/PrayerView.swift` and delete it — it was a temporary root (`ComposeView(fileName: "commonPrayers/kauma.json", ...)`) used only to prove the base iOS build worked; `HomeTabRootView` (Step 3) is its replacement as the real Home-tab landing screen.

```swift
// iosApp/MalankaraOrthodoxLiturgica/MalankaraOrthodoxLiturgicaApp.swift
import SwiftUI
import sharedKit

@main
struct MalankaraOrthodoxLiturgicaApp: App {
    @StateObject private var router = AppRouter()

    init() {
        SharedKit.shared.initialize()
    }

    var body: some Scene {
        WindowGroup {
            RootTabView()
                .environmentObject(router)
                .onOpenURL { url in
                    if let route = AppRoute(url: url) {
                        router.push(route)
                    }
                }
        }
    }
}
```

- [ ] **Step 7: Build (full app build, not just Kotlin) and verify**

Run the Global Constraints build command. Expected: `** BUILD SUCCEEDED **`. This will still fail to fully build until Tasks 6–14 provide the referenced-but-not-yet-created views (`CalendarTabRootView`, `BibleTabRootView`, etc.) — **create empty placeholder `View`s for each of the not-yet-built destinations right now** so the build is green at the end of this task, to be replaced (not left as dead code) in their respective later tasks:

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift
// Temporary stand-ins, each replaced by its real implementation in a later task:
// CalendarTabRootView/BibleTabRootView (Task 8), BibleReaderPushedView/
// BibleBookPushedView/BibleChapterPushedView (Task 8), SettingsPushedView/
// AboutPushedView/OnboardingComposeView (Task 11), SongPushedView (Task 13),
// QrScannerPushedView (Task 14).
import SwiftUI

struct CalendarTabRootView: View { var body: some View { Text("Calendar") } }
struct BibleTabRootView: View { var body: some View { Text("Bible") } }
struct BibleReaderPushedView: View { var body: some View { Text("Bible Reader") } }
struct BibleBookPushedView: View { let bookIndex: Int; var body: some View { Text("Bible Book \(bookIndex)") } }
struct BibleChapterPushedView: View {
    let bookIndex: Int
    let chapterIndex: Int
    var body: some View { Text("Bible Chapter \(bookIndex)/\(chapterIndex)") }
}
struct SettingsPushedView: View { var body: some View { Text("Settings") } }
struct AboutPushedView: View { var body: some View { Text("About") } }
struct OnboardingComposeView: View {
    let onNavigateToHome: () -> Void
    var body: some View { Button("Skip Onboarding", action: onNavigateToHome) }
}
struct SongPushedView: View { let route: String; var body: some View { Text("Song \(route)") } }
struct QrScannerPushedView: View { var body: some View { Text("QR Scanner") } }
```

Run the Global Constraints build command again. Expected: `** BUILD SUCCEEDED **`.

- [ ] **Step 8: Deploy and screenshot-verify**

Run the Global Constraints deploy/screenshot loop. Confirm via the screenshot: 4 tabs visible at the bottom (Home/Pray Now/Calendar/Bible), Home tab shows real prayer content (not a placeholder), tapping into a prayer section and back works.

- [ ] **Step 9: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add iosApp/MalankaraOrthodoxLiturgica/
git status  # confirm ContentView.swift and PrayerView.swift show as deleted
git commit -m "feat(ios): wire TabView + NavigationStack shell for the 5 existing screens"
```

---

### Task 6: Kotlin — register `bibleModule`, add Bible/BibleBook/BibleChapter bridge functions

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/InitKoin.kt`
- Modify: `shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt`

**Interfaces:**
- Consumes: `BibleViewModel` (`feature/bible-kmp`, DI'd via `feature/bible-kmp/.../di/KoinBibleModule.kt`'s `bibleModule`), `BibleScreen`/`BibleBookScreen`/`BibleChapterScreen` (`feature/bible-kmp/.../screens/`).
- Produces: `getBibleViewController(onBibleNavigate: (Int) -> Unit, onChromeStateChanged: (String, Boolean) -> Unit)`, `getBibleBookViewController(bookIndex: Int, onBibleNavigate: (Int, Int) -> Unit, onChromeStateChanged: (String, Boolean) -> Unit)`, `getBibleChapterViewController(bookIndex: Int, chapterIndex: Int, onChromeStateChanged: (String, Boolean) -> Unit)`.

- [ ] **Step 1: Register `bibleModule`**

Read `shared/src/commonMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/InitKoin.kt`, then modify:

```kotlin
package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.data.bible.di.bibleDataModule
import com.paradox543.malankaraorthodoxliturgica.data.calendar.di.calendarDataModule
import com.paradox543.malankaraorthodoxliturgica.data.core.di.dataCoreBridgeModule
import com.paradox543.malankaraorthodoxliturgica.data.prayer.di.prayerDataModule
import com.paradox543.malankaraorthodoxliturgica.data.translations.di.translationsDataModule
import com.paradox543.malankaraorthodoxliturgica.di.useCaseModule
import com.paradox543.malankaraorthodoxliturgica.feature.bible.di.bibleModule
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.di.calendarModule
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.di.prayerModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            bibleDataModule,
            calendarDataModule,
            dataCoreBridgeModule,
            prayerDataModule,
            translationsDataModule,
            useCaseModule,
            prayerModule,
            calendarModule,
            bibleModule,
            sharedModule,
            *platformModules().toTypedArray()
        )
    }
}


expect fun platformModules(): List<Module>
```

- [ ] **Step 2: Add the 3 Bible bridge functions to `Platform.ios.kt`**

Read the current file (as left by Task 4), then append these imports and functions:

```kotlin
// add to the import block at the top:
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleBookScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleChapterScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.viewmodel.BibleViewModel
```

```kotlin
// append to the end of the file:
@Composable
fun BibleScreenWrapper(
    onBibleNavigate: (Int) -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val bibleViewModel: BibleViewModel = koin.get()

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
    val koin = getKoin()
    val bibleViewModel: BibleViewModel = koin.get()

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
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val bibleViewModel: BibleViewModel = koin.get()

    BibleChapterScreen(
        bibleViewModel = bibleViewModel,
        bookIndex = bookIndex,
        chapterIndex = chapterIndex,
        contentPadding = PaddingValues(0.dp),
        onQrDialogShow = { _, _ -> "" },
        routeFactory = { ref -> "bible/${ref.bookIndex}/${ref.chapterIndex}" },
        onScaffoldStateChanged = { state ->
            val (title, showFab) = state.toChromeState()
            onChromeStateChanged(title, showFab)
        }
    )
}

fun getBibleChapterViewController(
    bookIndex: Int,
    chapterIndex: Int,
    onChromeStateChanged: (String, Boolean) -> Unit
): UIViewController = ComposeUIViewController {
    BibleChapterScreenWrapper(bookIndex, chapterIndex, onChromeStateChanged)
}
```

- [ ] **Step 3: Build to verify Kotlin compiles**

Run: `cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica && ./gradlew :shared:compileKotlinIosSimulatorArm64 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 4: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add shared/src/commonMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/InitKoin.kt shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt
git commit -m "feat(ios): add Bible/BibleBook/BibleChapter Kotlin bridge functions"
```

---

### Task 7: Kotlin — Calendar and BibleReader bridge functions

**Files:**
- Modify: `shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt`

**Interfaces:**
- Consumes: `CalendarViewModel` (already available — `calendarModule` is already registered in `InitKoin.kt`), `CalendarLiturgicalSeasonScreen` and `BibleReadingScreen` (both `feature/calendar-kmp/.../screens/`).
- Produces: `getCalendarViewController(onPrayerNavigate: (String) -> Unit, onBibleNavigate: () -> Unit, onChromeStateChanged: (String, Boolean) -> Unit)`, `getBibleReaderViewController(onChromeStateChanged: (String, Boolean) -> Unit)`.

- [ ] **Step 1: Add the 2 Calendar/BibleReader bridge functions to `Platform.ios.kt`**

Read the current file, then add this import:

```kotlin
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens.BibleReadingScreen
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens.CalendarLiturgicalSeasonScreen
```

Append:

```kotlin
@Composable
fun CalendarScreenWrapper(
    onPrayerNavigate: (String) -> Unit,
    onBibleNavigate: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val calendarViewModel: CalendarViewModel = koin.get()

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
    val koin = getKoin()
    val calendarViewModel: CalendarViewModel = koin.get()

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
```

- [ ] **Step 2: Build to verify Kotlin compiles**

Run: `cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica && ./gradlew :shared:compileKotlinIosSimulatorArm64 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt
git commit -m "feat(ios): add Calendar and BibleReader Kotlin bridge functions"
```

---

### Task 8: Swift — Bible/Calendar tab roots + pushed Bible screens

**Files:**
- Create: `iosApp/MalankaraOrthodoxLiturgica/BibleComposeView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/BibleBookComposeView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/BibleChapterComposeView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/CalendarComposeView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/BibleReaderComposeView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift` (remove the 5 replaced placeholders: `CalendarTabRootView`, `BibleTabRootView`, `BibleReaderPushedView`, `BibleBookPushedView`, `BibleChapterPushedView`)
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/CalendarTabRootView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/BibleTabRootView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/BibleReaderPushedView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/BibleBookPushedView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/BibleChapterPushedView.swift`

**Interfaces:**
- Consumes: `getBibleViewController`/`getBibleBookViewController`/`getBibleChapterViewController`/`getCalendarViewController`/`getBibleReaderViewController` (Tasks 6–7), `AppRouter`/`ChromeState` (Tasks 2/5).

- [ ] **Step 1: `UIViewControllerRepresentable` wrappers**

```swift
// iosApp/MalankaraOrthodoxLiturgica/BibleComposeView.swift
import SwiftUI
import sharedKit

struct BibleComposeView: UIViewControllerRepresentable {
    let onBibleNavigate: (Int) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getBibleViewController(
            onBibleNavigate: { bookIndex in onBibleNavigate(bookIndex.intValue) },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/BibleBookComposeView.swift
import SwiftUI
import sharedKit

struct BibleBookComposeView: UIViewControllerRepresentable {
    let bookIndex: Int
    let onBibleNavigate: (Int, Int) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getBibleBookViewController(
            bookIndex: Int32(bookIndex),
            onBibleNavigate: { bookIndex, chapterIndex in
                onBibleNavigate(bookIndex.intValue, chapterIndex.intValue)
            },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/BibleChapterComposeView.swift
import SwiftUI
import sharedKit

struct BibleChapterComposeView: UIViewControllerRepresentable {
    let bookIndex: Int
    let chapterIndex: Int
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getBibleChapterViewController(
            bookIndex: Int32(bookIndex),
            chapterIndex: Int32(chapterIndex),
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/CalendarComposeView.swift
import SwiftUI
import sharedKit

struct CalendarComposeView: UIViewControllerRepresentable {
    let onPrayerNavigate: (String) -> Void
    let onBibleNavigate: () -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getCalendarViewController(
            onPrayerNavigate: onPrayerNavigate,
            onBibleNavigate: onBibleNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/BibleReaderComposeView.swift
import SwiftUI
import sharedKit

struct BibleReaderComposeView: UIViewControllerRepresentable {
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getBibleReaderViewController(
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

- [ ] **Step 2: Remove the 5 now-replaced placeholders from `PlaceholderScreens.swift`**

Read `iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift`, then remove the `CalendarTabRootView`, `BibleTabRootView`, `BibleReaderPushedView`, `BibleBookPushedView`, `BibleChapterPushedView` struct declarations (keep `SettingsPushedView`, `AboutPushedView`, `OnboardingComposeView`, `SongPushedView`, `QrScannerPushedView` — those are replaced in later tasks) and update its header comment to reflect the remaining 5.

- [ ] **Step 3: Real tab-root / pushed views**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/CalendarTabRootView.swift
import SwiftUI

struct CalendarTabRootView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        CalendarComposeView(
            onPrayerNavigate: { router.push(.prayer($0)) },
            onBibleNavigate: { router.push(.bibleReader) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/BibleTabRootView.swift
import SwiftUI

struct BibleTabRootView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        BibleComposeView(
            onBibleNavigate: { router.push(.bibleBook(bookIndex: $0)) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/BibleReaderPushedView.swift
import SwiftUI

struct BibleReaderPushedView: View {
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        BibleReaderComposeView(chromeState: chromeState)
            .ignoresSafeArea(edges: .bottom)
            .navigationBarTitleDisplayMode(.inline)
    }
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/BibleBookPushedView.swift
import SwiftUI

struct BibleBookPushedView: View {
    let bookIndex: Int
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        BibleBookComposeView(
            bookIndex: bookIndex,
            onBibleNavigate: { book, chapter in router.push(.bibleChapter(bookIndex: book, chapterIndex: chapter)) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/BibleChapterPushedView.swift
import SwiftUI

struct BibleChapterPushedView: View {
    let bookIndex: Int
    let chapterIndex: Int
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        BibleChapterComposeView(bookIndex: bookIndex, chapterIndex: chapterIndex, chromeState: chromeState)
            .ignoresSafeArea(edges: .bottom)
            .navigationBarTitleDisplayMode(.inline)
    }
}
```

- [ ] **Step 4: Build and deploy/screenshot-verify**

Run the Global Constraints build command, then the deploy/screenshot loop. Confirm via screenshot: Bible tab shows the book list; tapping a book pushes chapters; Calendar tab shows the liturgical season screen.

- [ ] **Step 5: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add iosApp/MalankaraOrthodoxLiturgica/
git commit -m "feat(ios): wire Bible and Calendar tabs into the navigation shell"
```

---

### Task 9: Kotlin — iOS `ShareService`, register `SoundModeCapability`/`AppInfoProvider`/`settingsModule`/`onboardingModule`

**Files:**
- Create: `shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/IOSShareService.kt`
- Modify: `shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/IOSPlatformModule.kt`
- Modify: `shared/src/commonMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/InitKoin.kt`

**Interfaces:**
- Consumes: `ShareService`/`SoundModeCapability`/`AppInfoProvider` interfaces (`core/platform-kmp`, `core/app-info`), existing `IOSSoundModeCapability`/`IOSAppInfoProvider` classes.
- Produces: `IOSShareService: ShareService` registered in Koin; `settingsModule`/`onboardingModule` registered in `initKoin()`.

- [ ] **Step 1: `IOSShareService`**

`ShareService` has zero iOS implementation anywhere today (Android's `ShareServiceImpl` uses `Intent.ACTION_SEND`, Android-only). There's no live App Store listing for this app yet, so there's no store URL to append — this shares just the message text via `UIActivityViewController`, a deliberate, real, functioning scope decision (not a stub) given that constraint.

```kotlin
// shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/IOSShareService.kt
package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

class IOSShareService(
    private val analyticsService: AnalyticsService,
) : ShareService {
    override fun shareAppLink(
        shareSubject: String,
        shareMessage: String,
    ) {
        val shareText = shareMessage.ifEmpty { shareSubject }
        val activityViewController = UIActivityViewController(
            activityItems = listOf(shareText),
            applicationActivities = null,
        )
        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?.presentViewController(activityViewController, animated = true, completion = null)
        analyticsService.logEvent(AnalyticsEvent.ShareApp)
    }
}
```

- [ ] **Step 2: Register `IOSShareService`, `IOSSoundModeCapability`, `IOSAppInfoProvider` in `IOSPlatformModule.kt`**

Read the current file, then modify:

```kotlin
// shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/IOSPlatformModule.kt
package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.IOSInAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.IOSSoundModeCapability
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeCapability
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider
import com.paradox543.malankaraorthodoxliturgica.info.IOSAppInfoProvider
import org.koin.dsl.module

val iosPlatformModule = module {
    single<InAppReviewManager> { IOSInAppReviewManager() }
    single<AnalyticsService> {
        object : AnalyticsService {
            override fun logEvent(event: com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent) {
                // No-op or print for iOS
            }
        }
    }
    single<SoundModeCapability> { IOSSoundModeCapability() }
    single<AppInfoProvider> { IOSAppInfoProvider() }
    single<ShareService> { IOSShareService(analyticsService = get()) }
}
```

- [ ] **Step 3: Register `settingsModule`/`onboardingModule` in `InitKoin.kt`**

Read the current file (as left by Task 6), then modify:

```kotlin
package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.data.bible.di.bibleDataModule
import com.paradox543.malankaraorthodoxliturgica.data.calendar.di.calendarDataModule
import com.paradox543.malankaraorthodoxliturgica.data.core.di.dataCoreBridgeModule
import com.paradox543.malankaraorthodoxliturgica.data.prayer.di.prayerDataModule
import com.paradox543.malankaraorthodoxliturgica.data.translations.di.translationsDataModule
import com.paradox543.malankaraorthodoxliturgica.di.useCaseModule
import com.paradox543.malankaraorthodoxliturgica.feature.bible.di.bibleModule
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.di.calendarModule
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.di.onboardingModule
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.di.prayerModule
import com.paradox543.malankaraorthodoxliturgica.feature.settings.di.settingsModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            bibleDataModule,
            calendarDataModule,
            dataCoreBridgeModule,
            prayerDataModule,
            translationsDataModule,
            useCaseModule,
            prayerModule,
            calendarModule,
            bibleModule,
            settingsModule,
            onboardingModule,
            sharedModule,
            *platformModules().toTypedArray()
        )
    }
}


expect fun platformModules(): List<Module>
```

- [ ] **Step 4: Build to verify Kotlin compiles**

Run: `cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica && ./gradlew :shared:compileKotlinIosSimulatorArm64 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/IOSShareService.kt shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/IOSPlatformModule.kt shared/src/commonMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/di/InitKoin.kt
git commit -m "feat(ios): add IOSShareService, register SoundModeCapability/AppInfoProvider/settings+onboarding modules"
```

---

### Task 10: Kotlin — Settings/About/Onboarding bridge functions

**Files:**
- Modify: `shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt`

**Interfaces:**
- Consumes: `SettingsScreen`/`AboutScreen` (`feature/settings-kmp`), `OnboardingScreen` (`feature/onboarding-kmp`), `SettingsViewModel`/`OnboardingViewModel`, `ShareService`/`AppInfoProvider` (now registered per Task 9).
- Produces: `getSettingsViewController(onNavigateToAbout: () -> Unit, onChromeStateChanged: (String, Boolean) -> Unit)`, `getAboutViewController(appVersion: String, onDeveloperContact: () -> Unit, onExternalLinkClick: (String) -> Unit, onChromeStateChanged: (String, Boolean) -> Unit)`, `getOnboardingViewController(onNavigateToHome: () -> Unit)`, `getOnboardingCompleted(onResult: (Boolean) -> Unit)`.

- [ ] **Step 1: Add the 4 functions to `Platform.ios.kt`**

Read the current file, then add these imports:

```kotlin
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens.OnboardingScreen
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.settings.screens.AboutScreen
import com.paradox543.malankaraorthodoxliturgica.feature.settings.screens.SettingsScreen
import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
```

Append:

```kotlin
@Composable
fun SettingsScreenWrapper(
    onNavigateToAbout: () -> Unit,
    onChromeStateChanged: (String, Boolean) -> Unit
) {
    val koin = getKoin()
    val settingsViewModel: SettingsViewModel = koin.get()
    val shareService: ShareService = koin.get()

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
    val koin = getKoin()
    val onboardingViewModel: OnboardingViewModel = koin.get()

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
```

- [ ] **Step 2: Build to verify Kotlin compiles**

Run: `cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica && ./gradlew :shared:compileKotlinIosSimulatorArm64 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt
git commit -m "feat(ios): add Settings/About/Onboarding Kotlin bridge functions"
```

---

### Task 11: Swift — Settings/About/Onboarding screens + gear toolbar + onboarding gating

**Files:**
- Create: `iosApp/MalankaraOrthodoxLiturgica/SettingsComposeView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/AboutComposeView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/OnboardingComposeView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift` (remove `SettingsPushedView`, `AboutPushedView`, `OnboardingComposeView` placeholders)
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/SettingsPushedView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/AboutPushedView.swift`
- Create: `iosApp/MalankaraOrthodoxLiturgica/GearToolbarModifier.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Screens/HomeTabRootView.swift`, `PrayNowTabRootView.swift`, `CalendarTabRootView.swift`, `BibleTabRootView.swift`, `SectionPushedView.swift`, `PrayerPushedView.swift`, `IndexPushedView.swift`, `BibleReaderPushedView.swift`, `BibleBookPushedView.swift`, `BibleChapterPushedView.swift` (attach the gear toolbar)
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRouter.swift` (wire real onboarding-completed check)
- Modify: `iosApp/MalankaraOrthodoxLiturgica/MalankaraOrthodoxLiturgicaApp.swift` (query onboarding status on launch)

**Interfaces:**
- Consumes: `getSettingsViewController`/`getAboutViewController`/`getOnboardingViewController`/`getOnboardingCompleted` (Task 10).

- [ ] **Step 1: `UIViewControllerRepresentable` wrappers**

```swift
// iosApp/MalankaraOrthodoxLiturgica/SettingsComposeView.swift
import SwiftUI
import sharedKit

struct SettingsComposeView: UIViewControllerRepresentable {
    let onNavigateToAbout: () -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getSettingsViewController(
            onNavigateToAbout: onNavigateToAbout,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/AboutComposeView.swift
import SwiftUI
import sharedKit

struct AboutComposeView: UIViewControllerRepresentable {
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
        return Platform_iosKt.getAboutViewController(
            appVersion: version,
            onDeveloperContact: {
                if let url = URL(string: "mailto:dev@dquantix.com") {
                    UIApplication.shared.open(url)
                }
            },
            onExternalLinkClick: { link in
                if let url = URL(string: link) {
                    UIApplication.shared.open(url)
                }
            },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/OnboardingComposeView.swift
import SwiftUI
import sharedKit

struct OnboardingComposeView: UIViewControllerRepresentable {
    let onNavigateToHome: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getOnboardingViewController(onNavigateToHome: onNavigateToHome)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

- [ ] **Step 2: Remove the 3 now-replaced placeholders from `PlaceholderScreens.swift`**

Read `iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift`, remove `SettingsPushedView`, `AboutPushedView`, `OnboardingComposeView`. Only `SongPushedView`/`QrScannerPushedView` remain — update the header comment accordingly.

- [ ] **Step 3: Pushed views**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/SettingsPushedView.swift
import SwiftUI

struct SettingsPushedView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        SettingsComposeView(
            onNavigateToAbout: { router.push(.about) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
```

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/AboutPushedView.swift
import SwiftUI

struct AboutPushedView: View {
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        AboutComposeView(chromeState: chromeState)
            .ignoresSafeArea(edges: .bottom)
            .navigationBarTitleDisplayMode(.inline)
    }
}
```

- [ ] **Step 4: Gear toolbar modifier — global settings entry point**

```swift
// iosApp/MalankaraOrthodoxLiturgica/GearToolbarModifier.swift
import SwiftUI

/// Mirrors Android's shared `TopNavBar`, which shows a settings gear on
/// every screen except Settings itself (`NavGraph.kt`: `showSettings =
/// currentRoute != AppScreen.Settings.route`). On iOS the "current route"
/// check is just whether this modifier is applied at all — screens that
/// shouldn't show it (Settings itself) simply don't attach it.
struct GearToolbarModifier: ViewModifier {
    @EnvironmentObject var router: AppRouter

    func body(content: Content) -> some View {
        content.toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    router.push(.settings)
                } label: {
                    Image(systemName: "gearshape")
                }
            }
        }
    }
}

extension View {
    func withSettingsGear() -> some View {
        modifier(GearToolbarModifier())
    }
}
```

- [ ] **Step 5: Attach `.withSettingsGear()` to every screen except Settings**

Read each file, then add `.withSettingsGear()` to the modifier chain (after `.navigationBarTitleDisplayMode(.inline)`) in: `HomeTabRootView.swift`, `PrayNowTabRootView.swift`, `CalendarTabRootView.swift`, `BibleTabRootView.swift`, `SectionPushedView.swift`, `PrayerPushedView.swift`, `IndexPushedView.swift`, `BibleReaderPushedView.swift`, `BibleBookPushedView.swift`, `BibleChapterPushedView.swift`, `AboutPushedView.swift`. Example for `HomeTabRootView.swift`:

```swift
struct HomeTabRootView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        HomeComposeView(
            onSectionNavigate: { router.push(.section(route: $0)) },
            onPrayerNavigate: { router.push(.prayer($0)) },
            onSongNavigate: { router.push(.song(route: $0)) },
            onPrayNowNavigate: { router.selectedTab = .prayNow },
            onIndexNavigate: { router.push(.index) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
    }
}
```

(`SettingsPushedView` does not get `.withSettingsGear()` — it's already Settings.)

- [ ] **Step 6: Wire real onboarding-completed check on launch**

Read `iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRouter.swift`, add:

```swift
// add to AppRouter class:
func checkOnboardingStatus() {
    Platform_iosKt.getOnboardingCompleted { completed in
        self.showOnboarding = !completed.boolValue
    }
}
```

Add `import sharedKit` to the top of `AppRouter.swift`.

Read `iosApp/MalankaraOrthodoxLiturgica/MalankaraOrthodoxLiturgicaApp.swift`, modify:

```swift
import SwiftUI
import sharedKit

@main
struct MalankaraOrthodoxLiturgicaApp: App {
    @StateObject private var router = AppRouter()

    init() {
        SharedKit.shared.initialize()
    }

    var body: some Scene {
        WindowGroup {
            RootTabView()
                .environmentObject(router)
                .onAppear { router.checkOnboardingStatus() }
                .onOpenURL { url in
                    if let route = AppRoute(url: url) {
                        router.push(route)
                    }
                }
        }
    }
}
```

- [ ] **Step 7: Build and deploy/screenshot-verify**

Run the Global Constraints build command, then the deploy/screenshot loop. Confirm: settings gear appears in the toolbar on Home; tapping it pushes Settings; from Settings, "About" (if present in the Compose UI) pushes About. Since this app has no prior onboarding completion recorded on this simulator, also confirm the onboarding cover appears on cold launch, and tapping through it dismisses it back to the tab shell.

- [ ] **Step 8: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add iosApp/MalankaraOrthodoxLiturgica/
git commit -m "feat(ios): wire Settings/About/Onboarding screens, global settings gear, onboarding gating"
```

---

### Task 12: Kotlin — song metadata bridge function

**Files:**
- Modify: `shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt`

**Interfaces:**
- Consumes: `PrayerNavViewModel.rootNode: StateFlow<PageNode>` (already available via existing `koin.get()` pattern).
- Produces: `data class SongMetadata(val filename: String)`, `fun getSongMetadata(route: String): SongMetadata?`.

`SongScreen`'s Android implementation depends on `androidx.media3` (ExoPlayer) and has no `commonMain` — not portable (see plan header). This bridge exposes only the data iOS's native player (Task 13) needs: the audio filename, resolved via the same `PageNode` tree lookup Android's `NavGraph.kt` uses for its `Song` route (`prayerRootNode.findByRoute(route)?.filename`).

- [ ] **Step 1: Add `getSongMetadata` to `Platform.ios.kt`**

Read the current file, then append:

```kotlin
data class SongMetadata(
    val filename: String
)

fun getSongMetadata(route: String): SongMetadata? {
    val koin = getKoin()
    val prayerNavViewModel: PrayerNavViewModel = koin.get()
    val node = prayerNavViewModel.rootNode.value.findByRoute(route) ?: return null
    val filename = node.filename ?: return null
    return SongMetadata(filename = filename)
}
```

- [ ] **Step 2: Build to verify Kotlin compiles**

Run: `cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica && ./gradlew :shared:compileKotlinIosSimulatorArm64 2>&1 | tail -40`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add shared/src/iosMain/kotlin/com/paradox543/malankaraorthodoxliturgica/shared/Platform.ios.kt
git commit -m "feat(ios): add song metadata Kotlin bridge function"
```

---

### Task 13: Native song player (AVAudioPlayer)

**Files:**
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/SongPlayerView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift` (remove `SongPushedView` placeholder)
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Navigation/RootTabView.swift` (point `.song` case at the new view)
- Test: `iosApp/MalankaraOrthodoxLiturgicaTests/SongPlayerViewModelTests.swift`

**Interfaces:**
- Consumes: `getSongMetadata(route:)` (Task 12).
- Produces: `final class SongPlayerViewModel: ObservableObject` with `@Published var isPlaying: Bool`, `@Published var currentTime: TimeInterval`, `@Published var duration: TimeInterval`, `func load(filename: String)`, `func togglePlayback()`; `struct SongPlayerView: View`.

- [ ] **Step 1: Write the failing test (pure playback-state logic, no real audio file needed)**

```swift
// iosApp/MalankaraOrthodoxLiturgicaTests/SongPlayerViewModelTests.swift
import Testing
@testable import MalankaraOrthodoxLiturgica

@MainActor
struct SongPlayerViewModelTests {
    @Test func startsNotPlaying() async throws {
        let viewModel = SongPlayerViewModel()
        #expect(viewModel.isPlaying == false)
    }

    @Test func loadingMissingFileLeavesNotPlayingAndSetsError() async throws {
        let viewModel = SongPlayerViewModel()
        viewModel.load(filename: "does-not-exist.mp3")
        #expect(viewModel.isPlaying == false)
        #expect(viewModel.loadError != nil)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: FAIL — `cannot find 'SongPlayerViewModel' in scope`.

- [ ] **Step 3: Write the implementation**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/SongPlayerView.swift
import SwiftUI
import AVFAudio
import sharedKit

@MainActor
final class SongPlayerViewModel: NSObject, ObservableObject, AVAudioPlayerDelegate {
    @Published var isPlaying: Bool = false
    @Published var currentTime: TimeInterval = 0
    @Published var duration: TimeInterval = 0
    @Published var loadError: String?

    private var player: AVAudioPlayer?
    private var progressTimer: Timer?

    /// Song audio assets live in the app bundle under `Songs/`, matching how
    /// Android's `SongScreen` resolves `songFilename` against its own bundled
    /// assets via `node.filename`.
    func load(filename: String) {
        loadError = nil
        guard let url = Bundle.main.url(forResource: filename, withExtension: nil, subdirectory: "Songs") else {
            loadError = "Song file not found: \(filename)"
            return
        }
        do {
            let newPlayer = try AVAudioPlayer(contentsOf: url)
            newPlayer.delegate = self
            newPlayer.prepareToPlay()
            player = newPlayer
            duration = newPlayer.duration
            currentTime = 0
        } catch {
            loadError = error.localizedDescription
        }
    }

    func togglePlayback() {
        guard let player else { return }
        if player.isPlaying {
            player.pause()
            isPlaying = false
            stopProgressTimer()
        } else {
            player.play()
            isPlaying = true
            startProgressTimer()
        }
    }

    private func startProgressTimer() {
        stopProgressTimer()
        progressTimer = Timer.scheduledTimer(withTimeInterval: 0.25, repeats: true) { [weak self] _ in
            guard let self, let player = self.player else { return }
            self.currentTime = player.currentTime
        }
    }

    private func stopProgressTimer() {
        progressTimer?.invalidate()
        progressTimer = nil
    }

    nonisolated func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        Task { @MainActor in
            self.isPlaying = false
            self.currentTime = 0
            self.stopProgressTimer()
        }
    }

    deinit {
        progressTimer?.invalidate()
    }
}

struct SongPlayerView: View {
    let route: String
    @StateObject private var viewModel = SongPlayerViewModel()
    @EnvironmentObject var router: AppRouter

    var body: some View {
        VStack(spacing: 24) {
            if let error = viewModel.loadError {
                Text(error).foregroundStyle(.secondary)
            } else {
                Text(formatted(viewModel.currentTime)) + Text(" / ") + Text(formatted(viewModel.duration))
                Button {
                    viewModel.togglePlayback()
                } label: {
                    Image(systemName: viewModel.isPlaying ? "pause.circle.fill" : "play.circle.fill")
                        .resizable()
                        .frame(width: 64, height: 64)
                }
            }
        }
        .padding()
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .onAppear {
            if let metadata = Platform_iosKt.getSongMetadata(route: route) {
                viewModel.load(filename: metadata.filename)
            } else {
                viewModel.loadError = "Song not found: \(route)"
            }
        }
    }

    private func formatted(_ time: TimeInterval) -> String {
        let minutes = Int(time) / 60
        let seconds = Int(time) % 60
        return String(format: "%d:%02d", minutes, seconds)
    }
}
```

- [ ] **Step 4: Point `.song` case at the real view, remove the placeholder**

Read `iosApp/MalankaraOrthodoxLiturgica/Navigation/RootTabView.swift`, change:
```swift
case .song(let songRoute): SongPushedView(route: songRoute)
```
to:
```swift
case .song(let songRoute): SongPlayerView(route: songRoute)
```

Read `iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift`, remove the `SongPushedView` struct (only `QrScannerPushedView` remains — remove the file's header comment reference to Song).

- [ ] **Step 5: Run test to verify it passes**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: PASS (2 tests — `startsNotPlaying`, `loadingMissingFileLeavesNotPlayingAndSetsError`; no bundled `.mp3` assets exist yet, so a real-playback test isn't possible without also adding audio assets, which is a content task outside this plan's scope).

- [ ] **Step 6: Build and deploy/screenshot-verify**

Run the Global Constraints build command, then the deploy/screenshot loop. Since no song audio assets exist in the bundle yet, expect the screen to show its "Song file not found" error state — confirm the screen renders (doesn't crash) and the error message is legible.

- [ ] **Step 7: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add iosApp/MalankaraOrthodoxLiturgica/ iosApp/MalankaraOrthodoxLiturgicaTests/SongPlayerViewModelTests.swift
git commit -m "feat(ios): add native AVAudioPlayer song player screen"
```

---

### Task 14: Native QR scanner (AVFoundation)

**Files:**
- Create: `iosApp/MalankaraOrthodoxLiturgica/Screens/QrScannerView.swift`
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift` (remove — file becomes empty, delete it)
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Navigation/RootTabView.swift` (point `.qrScanner` at the new view)
- Modify: `iosApp/MalankaraOrthodoxLiturgica-Info.plist` (add `NSCameraUsageDescription`)
- Modify: `iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRouter.swift` (add `route(fromScannedString:)`)
- Test: `iosApp/MalankaraOrthodoxLiturgicaTests/QrScanResultParsingTests.swift`

**Interfaces:**
- Consumes: `AppRoute(url:)` (Task 3), `AppRouter.push` (Task 2).
- Produces: `extension AppRouter { func route(fromScannedString scanned: String) -> Bool }` (returns whether the scanned string parsed to a known route — mirrors Android's `QrScannerView`'s `onNavigate: (String) -> Unit` contract, which only fires for strings that resolve to a real route). `struct QrScannerView: View`.

- [ ] **Step 1: Write the failing test for scanned-string routing (pure logic, no camera needed)**

```swift
// iosApp/MalankaraOrthodoxLiturgicaTests/QrScanResultParsingTests.swift
import Testing
@testable import MalankaraOrthodoxLiturgica

@MainActor
struct QrScanResultParsingTests {
    @Test func validDeepLinkStringNavigates() async throws {
        let router = AppRouter()
        router.selectedTab = .bible
        let handled = router.route(fromScannedString: "liturgica://bible/1/2")
        #expect(handled == true)
        #expect(router.biblePath == [.bibleChapter(bookIndex: 1, chapterIndex: 2)])
    }

    @Test func unrelatedStringDoesNotNavigate() async throws {
        let router = AppRouter()
        let handled = router.route(fromScannedString: "not a deep link")
        #expect(handled == false)
        #expect(router.homePath.isEmpty)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: FAIL — `value of type 'AppRouter' has no member 'route(fromScannedString:)'`.

- [ ] **Step 3: Add `route(fromScannedString:)` to `AppRouter`**

Read `iosApp/MalankaraOrthodoxLiturgica/Navigation/AppRouter.swift`, add:

```swift
// add to AppRouter class:
/// Mirrors Android's `QrScannerView`'s `onNavigate(route: String)` contract:
/// a successful scan re-enters the router exactly like a tapped deep link.
/// Returns whether the scanned string resolved to a known route.
func route(fromScannedString scanned: String) -> Bool {
    guard let url = URL(string: scanned), let route = AppRoute(url: url) else {
        return false
    }
    push(route)
    return true
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -40`
Expected: PASS (2 tests).

- [ ] **Step 5: `NSCameraUsageDescription`**

Read `iosApp/MalankaraOrthodoxLiturgica-Info.plist`, add:

```xml
	<key>NSCameraUsageDescription</key>
	<string>Used to scan QR codes that link to prayers, Bible chapters, and other content in the app.</string>
```

(inserted as a sibling of the existing `CFBundleURLTypes`/`CADisableMinimumFrameDurationOnPhone` keys, inside the same top-level `<dict>`).

- [ ] **Step 6: `QrScannerView` — `AVCaptureSession`-based scanner**

```swift
// iosApp/MalankaraOrthodoxLiturgica/Screens/QrScannerView.swift
import SwiftUI
import AVFoundation

final class QrScanCoordinator: NSObject, AVCaptureMetadataOutputObjectsDelegate {
    var onScan: ((String) -> Void)?
    private var hasScanned = false

    func metadataOutput(
        _ output: AVCaptureMetadataOutput,
        didOutput metadataObjects: [AVMetadataObject],
        from connection: AVCaptureConnection
    ) {
        guard !hasScanned,
              let object = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
              object.type == .qr,
              let stringValue = object.stringValue else { return }
        hasScanned = true
        onScan?(stringValue)
    }

    func reset() {
        hasScanned = false
    }
}

struct QrCaptureView: UIViewControllerRepresentable {
    let onScan: (String) -> Void

    func makeCoordinator() -> QrScanCoordinator {
        let coordinator = QrScanCoordinator()
        coordinator.onScan = onScan
        return coordinator
    }

    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = UIViewController()
        let session = AVCaptureSession()

        guard let device = AVCaptureDevice.default(for: .video),
              let input = try? AVCaptureDeviceInput(device: device),
              session.canAddInput(input) else {
            return viewController
        }
        session.addInput(input)

        let output = AVCaptureMetadataOutput()
        guard session.canAddOutput(output) else { return viewController }
        session.addOutput(output)
        output.setMetadataObjectsDelegate(context.coordinator, queue: .main)
        output.metadataObjectTypes = [.qr]

        let previewLayer = AVCaptureVideoPreviewLayer(session: session)
        previewLayer.videoGravity = .resizeAspectFill
        previewLayer.frame = UIScreen.main.bounds
        viewController.view.layer.addSublayer(previewLayer)

        DispatchQueue.global(qos: .userInitiated).async {
            session.startRunning()
        }

        return viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct QrScannerView: View {
    @EnvironmentObject var router: AppRouter
    @State private var scanFailedMessage: String?

    var body: some View {
        ZStack(alignment: .bottom) {
            QrCaptureView { scanned in
                if !router.route(fromScannedString: scanned) {
                    scanFailedMessage = "Not a recognized code"
                }
            }
            .ignoresSafeArea()

            if let message = scanFailedMessage {
                Text(message)
                    .padding()
                    .background(.ultraThinMaterial, in: Capsule())
                    .padding(.bottom, 32)
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationTitle("Scan QR Code")
    }
}
```

- [ ] **Step 7: Point `.qrScanner` at the real view, delete the placeholder file**

Read `iosApp/MalankaraOrthodoxLiturgica/Navigation/RootTabView.swift`, change:
```swift
case .qrScanner: QrScannerPushedView()
```
to:
```swift
case .qrScanner: QrScannerView()
```

Delete `iosApp/MalankaraOrthodoxLiturgica/Screens/PlaceholderScreens.swift` — it only contained `QrScannerPushedView` at this point (every other placeholder was already replaced in Tasks 8/11/13).

Wire the QR-scan entry point: add a toolbar/nav-bar-item to trigger `router.push(.qrScanner)` from `HomeTabRootView` (Android's shared FAB — the `showFab` chrome state already threaded through in Task 5 marks which screens want this). Read `iosApp/MalankaraOrthodoxLiturgica/Screens/HomeTabRootView.swift`, add:

```swift
struct HomeTabRootView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        HomeComposeView(
            onSectionNavigate: { router.push(.section(route: $0)) },
            onPrayerNavigate: { router.push(.prayer($0)) },
            onSongNavigate: { router.push(.song(route: $0)) },
            onPrayNowNavigate: { router.selectedTab = .prayNow },
            onIndexNavigate: { router.push(.index) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .overlay(alignment: .bottomTrailing) {
            if chromeState.showFab {
                Button {
                    router.push(.qrScanner)
                } label: {
                    Image(systemName: "qrcode.viewfinder")
                        .font(.title2)
                        .padding()
                        .background(.thinMaterial, in: Circle())
                }
                .padding()
            }
        }
    }
}
```

- [ ] **Step 8: Build and deploy/screenshot-verify**

Run the Global Constraints build command, then the deploy/screenshot loop. Confirm: build succeeds with the camera permission string present; tapping the QR FAB on Home pushes the scanner screen (note: the iOS Simulator has no real camera, so the live feed itself can't be verified on-device this way — verify the screen renders without crashing, and that a scan-failure/no-camera state doesn't crash the app; full camera verification requires a physical device, out of scope for simulator-based verification).

- [ ] **Step 9: Commit**

```bash
cd /Users/praneethm/Projects/sam/MalankaraOrthodoxLiturgica
git add iosApp/MalankaraOrthodoxLiturgica/ iosApp/MalankaraOrthodoxLiturgica-Info.plist iosApp/MalankaraOrthodoxLiturgicaTests/QrScanResultParsingTests.swift
git status  # confirm PlaceholderScreens.swift shows as deleted
git commit -m "feat(ios): add native AVFoundation QR scanner, wire FAB entry point"
```

---

### Task 15: End-to-end verification pass

**Files:** none (verification only).

- [ ] **Step 1: Full test suite**

Run: `xcodebuild test -project iosApp/MalankaraOrthodoxLiturgica.xcodeproj -scheme MalankaraOrthodoxLiturgica -destination "platform=iOS Simulator,name=iPhone 17" -only-testing:MalankaraOrthodoxLiturgicaTests 2>&1 | tail -60`
Expected: all tests from Tasks 1–3, 13, 14 PASS (19 tests total: 3 + 5 + 7 + 2 + 2).

- [ ] **Step 2: Full build**

Run the Global Constraints build command. Expected: `** BUILD SUCCEEDED **`.

- [ ] **Step 3: Deep link smoke test across every pattern**

```bash
xcrun simctl launch 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 com.paradox543.MalankaraOrthodoxLiturgica
sleep 2
for url in \
  "liturgica://home" \
  "liturgica://bible" \
  "liturgica://calendar" \
  "liturgica://index" \
  "liturgica://settings" \
  "liturgica://about" \
  "liturgica://section/morningPrayer" \
  "liturgica://prayer/morningPrayer/0" \
  "liturgica://bible/0" \
  "liturgica://bible/0/0"; do
  echo "=== $url ==="
  xcrun simctl openurl 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 "$url"
  sleep 1
  xcrun simctl io 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 screenshot "/tmp/deeplink_$(echo $url | tr -c 'a-zA-Z0-9' '_').png"
done
```

Review each screenshot: confirm the app lands on the expected screen (not a crash, not stuck on the previous screen) for each URL. Section/Prayer routes may show a "not found" state if `morningPrayer` isn't a real route in this build's content tree — acceptable; what's being verified is that the router *attempts* the correct navigation, not that every specific route ID exists in the shipped prayer content.

- [ ] **Step 4: Tab-reselect-clears-stack manual check**

Via the screenshot loop: launch the app, tap into Bible → a book → a chapter (3 taps deep), tap the Bible tab icon again (already active), screenshot. Confirm the screenshot shows the Bible book list (root), not the chapter screen — proving `handleTabReselect` popped the stack.

- [ ] **Step 5: Onboarding-cover dismiss persistence check**

```bash
xcrun simctl uninstall 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 com.paradox543.MalankaraOrthodoxLiturgica
APP_PATH="/Users/praneethm/Library/Developer/Xcode/DerivedData/MalankaraOrthodoxLiturgica-anvrclbqzojppzhkdynjqrrsmqxy/Build/Products/Debug-iphonesimulator/MalankaraOrthodoxLiturgica.app"
xcrun simctl install 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 "$APP_PATH"
xcrun simctl launch 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 com.paradox543.MalankaraOrthodoxLiturgica
sleep 2
xcrun simctl io 66FFC4D0-F7AD-42B9-B89A-E7A3F3E38228 screenshot /tmp/onboarding_first_launch.png
```
Confirm the screenshot shows the onboarding cover (fresh install, no prior completion recorded). Then manually tap through onboarding to completion in Simulator.app, relaunch (`xcrun simctl terminate ... && xcrun simctl launch ...`), screenshot again — confirm onboarding does **not** reappear, proving `getOnboardingCompleted` correctly reads persisted state.

- [ ] **Step 6: No commit for this task** — it's verification-only. If any check in Steps 1–5 fails, go back to the relevant earlier task, fix, and re-run this task's checks before considering the plan complete.
