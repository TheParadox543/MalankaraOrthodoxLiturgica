import SwiftUI
import sharedKit

@main
struct MalankaraOrthodoxLiturgicaApp: App {
    @StateObject private var router = AppRouter()

    init() {
        SharedKit.shared.initialize(nativeAnalyticsLogger: SwiftFirebaseAnalyticsLogger())
    }

    var body: some Scene {
        WindowGroup {
            RootTabView()
                .environmentObject(router)
                .onAppear { router.checkOnboardingStatus() }
                .onOpenURL { url in
                    if let route = AppRoute(url: url) {
                        router.push(route)
                    }
                }
        }
    }
}
