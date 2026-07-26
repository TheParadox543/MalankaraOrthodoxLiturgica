import SwiftUI

struct PrayerPushedView: View {
    let route: String
    let scroll: Int
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        ComposeView(
            fileName: route,
            onPrayerButtonClick: { _, _ in },
            chromeState: chromeState
        )
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
    }
}
