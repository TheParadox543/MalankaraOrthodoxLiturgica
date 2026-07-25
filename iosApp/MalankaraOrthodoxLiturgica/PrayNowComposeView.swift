import SwiftUI
import sharedKit

struct PrayNowComposeView: UIViewControllerRepresentable {
    let onPrayerNavigate: (String) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getPrayNowViewController(
            onPrayerNavigate: onPrayerNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
