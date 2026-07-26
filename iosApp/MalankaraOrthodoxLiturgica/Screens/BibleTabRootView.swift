import SwiftUI

struct BibleTabRootView: View {
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        BibleComposeView(
            onBibleNavigate: { router.push(.bibleBook(bookIndex: $0)) },
            chromeState: chromeState
        )
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .withQrFab(chromeState)
    }
}
