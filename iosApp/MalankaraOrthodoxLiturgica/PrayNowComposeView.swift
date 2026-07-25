import SwiftUI
import sharedKit

struct PrayNowComposeView: UIViewControllerRepresentable {
    let onPrayerNavigate: (String) -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getPrayNowViewController(
            onPrayerNavigate: onPrayerNavigate
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
