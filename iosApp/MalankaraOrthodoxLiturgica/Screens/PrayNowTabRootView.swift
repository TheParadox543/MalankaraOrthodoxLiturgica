import SwiftUI

struct PrayNowTabRootView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        PrayNowComposeView(
            onPrayerNavigate: { router.push(.prayer($0)) },
            chromeState: chromeState
        )
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .withQrFab(chromeState)
    }
}
