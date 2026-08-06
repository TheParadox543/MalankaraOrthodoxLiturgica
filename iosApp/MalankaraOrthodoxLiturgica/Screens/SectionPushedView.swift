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
            chromeState: chromeState,
            onBackNavigation: { router.pop() }
        )
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .withQrFab(chromeState)
    }
}
