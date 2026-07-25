import SwiftUI
import sharedKit

struct HomeComposeView: UIViewControllerRepresentable {
    let onSectionNavigate: (String) -> Void
    let onPrayerNavigate: (String) -> Void
    let onSongNavigate: (String) -> Void
    let onPrayNowNavigate: () -> Void
    let onIndexNavigate: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getHomeViewController(
            onSectionNavigate: onSectionNavigate,
            onPrayerNavigate: onPrayerNavigate,
            onSongNavigate: onSongNavigate,
            onPrayNowNavigate: onPrayNowNavigate,
            onIndexNavigate: onIndexNavigate
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
