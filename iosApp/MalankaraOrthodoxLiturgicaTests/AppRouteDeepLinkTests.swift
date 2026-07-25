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
