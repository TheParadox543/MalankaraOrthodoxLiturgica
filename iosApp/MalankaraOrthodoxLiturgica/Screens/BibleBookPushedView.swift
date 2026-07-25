import SwiftUI

struct BibleBookPushedView: View {
    let bookIndex: Int
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        BibleBookComposeView(
            bookIndex: bookIndex,
            onBibleNavigate: { book, chapter in router.push(.bibleChapter(bookIndex: book, chapterIndex: chapter)) },
            chromeState: chromeState
        )
        .ignoresSafeArea(edges: .bottom)
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
    }
}
