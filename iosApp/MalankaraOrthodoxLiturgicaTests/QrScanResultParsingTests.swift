import Testing
@testable import MalankaraOrthodoxLiturgica

@MainActor
struct QrScanResultParsingTests {
    @Test func validDeepLinkStringNavigates() async throws {
        let router = AppRouter()
        router.selectedTab = .bible
        let handled = router.route(fromScannedString: "app://liturgica/bible/1/2")
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
