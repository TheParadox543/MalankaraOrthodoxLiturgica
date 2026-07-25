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
