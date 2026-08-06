import SwiftUI
import sharedKit

struct SectionComposeView: UIViewControllerRepresentable {
    let route: String
    let onSectionNavigate: (String) -> Void
    let onPrayerNavigate: (String) -> Void
    let onSongNavigate: (String) -> Void
    let onIndexNavigate: () -> Void
    @ObservedObject var chromeState: ChromeState
    let onBackNavigation: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getSectionViewController(
            route: route,
            onSectionNavigate: onSectionNavigate,
            onPrayerNavigate: onPrayerNavigate,
            onSongNavigate: onSongNavigate,
            onIndexNavigate: onIndexNavigate,
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            },
            onBackNavigation: onBackNavigation
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
