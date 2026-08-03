import SwiftUI

struct AboutPushedView: View {
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        AboutComposeView(chromeState: chromeState)
            .navigationBarTitleDisplayMode(.inline)
            .withSettingsGear()
            .withQrFab(chromeState)
            .toolbar(.hidden, for: .tabBar)
    }
}
