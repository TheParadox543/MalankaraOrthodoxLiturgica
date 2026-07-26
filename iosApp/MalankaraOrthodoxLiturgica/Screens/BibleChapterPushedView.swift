import SwiftUI

struct BibleChapterPushedView: View {
    let bookIndex: Int
    let chapterIndex: Int
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        BibleChapterComposeView(bookIndex: bookIndex, chapterIndex: chapterIndex, chromeState: chromeState)
            .navigationBarTitleDisplayMode(.inline)
            .withSettingsGear()
    }
}
