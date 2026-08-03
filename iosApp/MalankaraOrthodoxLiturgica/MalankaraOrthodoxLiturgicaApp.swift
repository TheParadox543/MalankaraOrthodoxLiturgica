import SwiftUI
import FirebaseCore
import sharedKit

@main
struct MalankaraOrthodoxLiturgicaApp: App {
    @StateObject private var router = AppRouter()

    init() {
            FirebaseApp.configure()

            // Initialize SharedKit with native implementations
            SharedKit.shared.initialize(
                nativeAnalyticsLogger: SwiftFirebaseAnalyticsLogger(),
                nativeRemoteContentSource: SwiftFirebaseRemoteContentSource()
            )

            // Trigger synchronization
            Task {
                do {
                    try await SharedKit.shared.getSynchronizer().synchronize()
                } catch {
                    print("iOS Background Sync failed: \(error)")
                }
            }
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
