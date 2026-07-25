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
