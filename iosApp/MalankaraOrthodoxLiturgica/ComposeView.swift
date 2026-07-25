import SwiftUI
import sharedKit

struct ComposeView: UIViewControllerRepresentable {
    let fileName: String
    let onPrayerButtonClick: (String, Bool) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getPrayerViewController(
            route: fileName,
            onPrayerButtonClick: { link, replace in
                onPrayerButtonClick(link, replace.boolValue)
            },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
