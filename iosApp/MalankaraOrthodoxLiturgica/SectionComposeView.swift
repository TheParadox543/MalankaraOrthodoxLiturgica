import SwiftUI
import sharedKit

struct SectionComposeView: UIViewControllerRepresentable {
    let route: String
    let onSectionNavigate: (String) -> Void
    let onPrayerNavigate: (String) -> Void
    let onSongNavigate: (String) -> Void
    let onIndexNavigate: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getSectionViewController(
            route: route,
            onSectionNavigate: onSectionNavigate,
            onPrayerNavigate: onPrayerNavigate,
            onSongNavigate: onSongNavigate,
            onIndexNavigate: onIndexNavigate
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
