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
        case .qrScanner: QrScannerView()
        case .index: IndexPushedView()
        case .settings: SettingsPushedView()
        case .about: AboutPushedView()
        case .section(let sectionRoute): SectionPushedView(route: sectionRoute)
        case .prayer(let prayerRoute, let scroll): PrayerPushedView(route: prayerRoute, scroll: scroll)
        case .song(let songRoute): SongPlayerView(route: songRoute)
        case .bibleBook(let bookIndex): BibleBookPushedView(bookIndex: bookIndex)
        case .bibleChapter(let bookIndex, let chapterIndex):
            BibleChapterPushedView(bookIndex: bookIndex, chapterIndex: chapterIndex)
        }
    }
}
