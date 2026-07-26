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
        ComposeView(
            fileName: route,
            onPrayerButtonClick: { _, _ in },
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
                onPrev: { if let p = prevRoute { router.replace(.prayer(p)) } },
                onNext: { if let n = nextRoute { router.replace(.prayer(n)) } },
                onGenerateQr: { generateQr?() }
            )
        }
    }
}
