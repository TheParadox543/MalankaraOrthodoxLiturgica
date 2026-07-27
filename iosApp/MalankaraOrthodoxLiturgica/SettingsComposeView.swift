import SwiftUI
import sharedKit

struct SettingsComposeView: UIViewControllerRepresentable {
    let onNavigateToAbout: () -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getSettingsViewController(
            onNavigateToAbout: onNavigateToAbout,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
