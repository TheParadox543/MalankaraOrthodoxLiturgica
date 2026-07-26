import SwiftUI

struct BibleChapterPushedView: View {
    let bookIndex: Int
    let chapterIndex: Int
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()
    @State private var prevRoute: String?
    @State private var nextRoute: String?
    @State private var generateQr: (() -> Void)?

    var body: some View {
        BibleChapterComposeView(
            bookIndex: bookIndex,
            chapterIndex: chapterIndex,
            chromeState: chromeState,
            onSectionNavChanged: { prev, next, onGenerateQr in
                prevRoute = prev
                nextRoute = next
                generateQr = onGenerateQr
            }
        )
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .toolbar(.hidden, for: .tabBar)
        .safeAreaInset(edge: .bottom) {
            SectionNavBar(
                hasPrev: prevRoute != nil,
                hasNext: nextRoute != nil,
                onPrev: { pushChapter(prevRoute) },
                onNext: { pushChapter(nextRoute) },
                onGenerateQr: { generateQr?() }
            )
        }
    }

    /// `prevRoute`/`nextRoute` arrive as `"bookIndex/chapterIndex"`
    /// (matching Platform.ios.kt's `routeFactory`), not a full `AppRoute`,
    /// since the Kotlin side only knows the bare `BibleChapterRef` pair.
    private func pushChapter(_ raw: String?) {
        guard let raw, let slashIndex = raw.firstIndex(of: "/") else { return }
        let book = Int(raw[raw.startIndex..<slashIndex])
        let chapter = Int(raw[raw.index(after: slashIndex)...])
        guard let book, let chapter else { return }
        router.replace(.bibleChapter(bookIndex: book, chapterIndex: chapter))
    }
}
