import SwiftUI
import sharedKit

struct IndexComposeView: UIViewControllerRepresentable {
    let onPrayerNavigate: (String) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getIndexViewController(
            onPrayerNavigate: onPrayerNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
