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
        .withSettingsGear()
    }
}
