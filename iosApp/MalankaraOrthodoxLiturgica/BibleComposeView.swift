import SwiftUI
import sharedKit

struct BibleComposeView: UIViewControllerRepresentable {
    let onBibleNavigate: (Int) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getBibleViewController(
            onBibleNavigate: { bookIndex in onBibleNavigate(bookIndex.intValue) },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
