import SwiftUI
import sharedKit

struct ComposeView: UIViewControllerRepresentable {
    let fileName: String
    let scroll: Int
    let onPrayerButtonClick: (String, Bool) -> Void
    @ObservedObject var chromeState: ChromeState
    let onSectionNavChanged: (String?, String?, @escaping () -> Void) -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getPrayerViewController(
            route: fileName,
            scrollIndex: Int32(scroll),
            onPrayerButtonClick: { link, replace in
                onPrayerButtonClick(link, replace.boolValue)
            },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            },
            onSectionNavChanged: { prev, next, onGenerateQr in
                onSectionNavChanged(prev, next, { onGenerateQr() })
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
