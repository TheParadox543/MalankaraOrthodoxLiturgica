import SwiftUI

struct BibleReaderPushedView: View {
    @StateObject private var chromeState = ChromeState()

    var body: some View {
        BibleReaderComposeView(chromeState: chromeState)
            .navigationBarTitleDisplayMode(.inline)
            .withSettingsGear()
    }
}
