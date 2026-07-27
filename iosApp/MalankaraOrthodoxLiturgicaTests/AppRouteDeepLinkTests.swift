import Testing
import Foundation
@testable import MalankaraOrthodoxLiturgica

struct AppRouteDeepLinkTests {
    @Test func parsesSimpleRoutes() async throws {
        #expect(AppRoute(url: URL(string: "app://liturgica/home")!) == .home)
        #expect(AppRoute(url: URL(string: "app://liturgica/bible")!) == .bible)
        #expect(AppRoute(url: URL(string: "app://liturgica/calendar")!) == .calendar)
        #expect(AppRoute(url: URL(string: "app://liturgica/index")!) == .index)
        #expect(AppRoute(url: URL(string: "app://liturgica/settings")!) == .settings)
        #expect(AppRoute(url: URL(string: "app://liturgica/about")!) == .about)
    }

    @Test func parsesSectionRoute() async throws {
        #expect(AppRoute(url: URL(string: "app://liturgica/section/morningPrayer")!) == .section(route: "morningPrayer"))
    }

    @Test func parsesPrayerRouteWithScroll() async throws {
        #expect(AppRoute(url: URL(string: "app://liturgica/prayer/morning/40")!) == .prayer(route: "morning", scroll: 40))
    }

    @Test func parsesBibleBookVsBibleChapterByComponentCount() async throws {
        #expect(AppRoute(url: URL(string: "app://liturgica/bible/1")!) == .bibleBook(bookIndex: 1))
        #expect(AppRoute(url: URL(string: "app://liturgica/bible/1/2")!) == .bibleChapter(bookIndex: 1, chapterIndex: 2))
    }

    @Test func routesWithNoAndroidDeepLinkReturnNil() async throws {
        // onboarding, prayNow, bibleReader, qrScanner, song have no deep link
        // on Android either (AppScreen.kt constructor defaults deepLink to null).
        #expect(AppRoute(url: URL(string: "app://liturgica/onboarding")!) == nil)
        #expect(AppRoute(url: URL(string: "app://liturgica/prayNow")!) == nil)
        #expect(AppRoute(url: URL(string: "app://liturgica/song/aRoute")!) == nil)
    }

    @Test func unknownRouteTypeReturnsNil() async throws {
        #expect(AppRoute(url: URL(string: "app://liturgica/nonsense/path")!) == nil)
    }

    @Test func wrongSchemeOrHostReturnsNil() async throws {
        // Matches Android's intent filter (scheme="app" host="liturgica")
        // exactly — anything else, including the URL shape iOS used before
        // matching Android, must not parse.
        #expect(AppRoute(url: URL(string: "liturgica://prayer/morning/0")!) == nil)
        #expect(AppRoute(url: URL(string: "app://wronghost/home")!) == nil)
        #expect(AppRoute(url: URL(string: "https://liturgica/home")!) == nil)
    }

    @Test func malformedNumericSegmentsReturnNil() async throws {
        #expect(AppRoute(url: URL(string: "app://liturgica/prayer/morning/notANumber")!) == nil)
        #expect(AppRoute(url: URL(string: "app://liturgica/bible/notANumber")!) == nil)
    }
}
