import SwiftUI
import sharedKit

struct BibleChapterComposeView: UIViewControllerRepresentable {
    let bookIndex: Int
    let chapterIndex: Int
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getBibleChapterViewController(
            bookIndex: Int32(bookIndex),
            chapterIndex: Int32(chapterIndex),
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
