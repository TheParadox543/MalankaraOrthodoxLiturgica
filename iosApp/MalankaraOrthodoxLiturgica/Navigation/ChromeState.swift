import Foundation
import Combine

/// Republishes a Kotlin bridge function's flattened `onChromeStateChanged`
/// callback as `@Published` state a SwiftUI wrapper view can read to drive
/// its toolbar/FAB — the Swift-side half of the Chrome design in
/// `docs/superpowers/specs/2026-07-25-ios-navigation-design.md`.
final class ChromeState: ObservableObject {
    @Published var title: String = ""
    @Published var showFab: Bool = false

    func update(title: String, showFab: Bool) {
        self.title = title
        self.showFab = showFab
    }
}
