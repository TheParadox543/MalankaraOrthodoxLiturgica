import SwiftUI

struct AboutPushedView: View {
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        AboutComposeView(chromeState: chromeState)
            .ignoresSafeArea(edges: .bottom)
            .navigationBarTitleDisplayMode(.inline)
            .withSettingsGear()
    }
}
