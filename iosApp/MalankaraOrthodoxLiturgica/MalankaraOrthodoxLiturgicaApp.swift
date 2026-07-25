import SwiftUI
import sharedKit

@main
struct MalankaraOrthodoxLiturgicaApp: App {
    @StateObject private var router = AppRouter()

    init() {
        SharedKit.shared.initialize()
    }

    var body: some Scene {
        WindowGroup {
            RootTabView()
                .environmentObject(router)
                .onOpenURL { url in
                    if let route = AppRoute(url: url) {
                        router.push(route)
                    }
                }
        }
    }
}
