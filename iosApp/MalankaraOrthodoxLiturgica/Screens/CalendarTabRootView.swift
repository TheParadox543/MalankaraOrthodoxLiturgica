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
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .withQrFab(chromeState)
    }
}
