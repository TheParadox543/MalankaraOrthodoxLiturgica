import SwiftUI

struct SettingsPushedView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        SettingsComposeView(
            onNavigateToAbout: { router.push(.about) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
    }
}
