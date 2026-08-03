import SwiftUI
import sharedKit

struct CalendarComposeView: UIViewControllerRepresentable {
    let onPrayerNavigate: (String) -> Void
    let onBibleNavigate: () -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getCalendarViewController(
            onPrayerNavigate: onPrayerNavigate,
            onBibleNavigate: onBibleNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
