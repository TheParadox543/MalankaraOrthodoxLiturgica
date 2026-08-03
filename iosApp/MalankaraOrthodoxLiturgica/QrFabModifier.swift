import SwiftUI

/// Mirrors Android's shared `QrFabScan`, which every `ScaffoldUiState.Standard`
/// screen renders whenever `showFab` is true (default `true` — Calendar,
/// Settings, and About are the screens that opt out, via
/// `ScaffoldUiState.Standard(showFab = false)`). On iOS `chromeState.showFab`
/// already carries that per-screen value across the bridge; this modifier is
/// what actually renders the FAB from it, applied uniformly instead of only
/// on Home.
struct QrFabModifier: ViewModifier {
    @EnvironmentObject var router: AppRouter
    @ObservedObject var chromeState: ChromeState

    func body(content: Content) -> some View {
        content.overlay(alignment: .bottomTrailing) {
            if chromeState.showFab {
                Button {
                    router.push(.qrScanner)
                } label: {
                    Image(systemName: "qrcode.viewfinder")
                        .font(.title2)
                        .padding()
                        .background(.thinMaterial, in: Circle())
                }
                .padding()
            }
        }
    }
}

extension View {
    func withQrFab(_ chromeState: ChromeState) -> some View {
        modifier(QrFabModifier(chromeState: chromeState))
    }
}
