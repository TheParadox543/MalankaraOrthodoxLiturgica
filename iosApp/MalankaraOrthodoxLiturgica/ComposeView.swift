import SwiftUI
import sharedKit

struct ComposeView: UIViewControllerRepresentable {
    let fileName: String
    let onPrayerButtonClick: (String, Bool) -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getPrayerViewController(
            fileName: fileName,
            onPrayerButtonClick: { link, replace in
                onPrayerButtonClick(link, replace.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
