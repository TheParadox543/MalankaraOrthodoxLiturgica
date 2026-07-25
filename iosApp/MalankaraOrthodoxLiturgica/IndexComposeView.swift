import SwiftUI
import sharedKit

struct IndexComposeView: UIViewControllerRepresentable {
    let onPrayerNavigate: (String) -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getIndexViewController(
            onPrayerNavigate: onPrayerNavigate
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
