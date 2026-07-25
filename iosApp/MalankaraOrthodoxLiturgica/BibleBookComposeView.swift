import SwiftUI
import sharedKit

struct BibleBookComposeView: UIViewControllerRepresentable {
    let bookIndex: Int
    let onBibleNavigate: (Int, Int) -> Void
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getBibleBookViewController(
            bookIndex: Int32(bookIndex),
            onBibleNavigate: { bookIndex, chapterIndex in
                onBibleNavigate(bookIndex.intValue, chapterIndex.intValue)
            },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
