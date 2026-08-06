import SwiftUI

struct PrayerPushedView: View {
    let route: String
    let scroll: Int
    @EnvironmentObject var router: AppRouter
    @StateObject private var chromeState = ChromeState()
    @State private var prevRoute: String?
    @State private var nextRoute: String?
    @State private var generateQr: (() -> Void)?

    var body: some View {
        VStack(spacing: 0) {
            ComposeView(
                fileName: route,
                scroll: scroll,
                onPrayerButtonClick: { _, _ in },
                chromeState: chromeState,
                onSectionNavChanged: { prev, next, onGenerateQr in
                    prevRoute = prev
                    nextRoute = next
                    generateQr = onGenerateQr
                },
                onBackNavigation: { router.pop() }
            )
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            SectionNavBar(
                hasPrev: prevRoute != nil,
                hasNext: nextRoute != nil,
                onPrev: { if let p = prevRoute { router.replace(.prayer(p)) } },
                onNext: { if let n = nextRoute { router.replace(.prayer(n)) } },
                onGenerateQr: { generateQr?() }
            )
        }
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .toolbar(.hidden, for: .tabBar)
    }
}
