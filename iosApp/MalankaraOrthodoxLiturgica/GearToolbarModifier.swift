import SwiftUI

/// Mirrors Android's shared `TopNavBar`, which shows a settings gear on
/// every screen except Settings itself (`NavGraph.kt`: `showSettings =
/// currentRoute != AppScreen.Settings.route`). On iOS the "current route"
/// check is just whether this modifier is applied at all — screens that
/// shouldn't show it (Settings itself) simply don't attach it.
struct GearToolbarModifier: ViewModifier {
    @EnvironmentObject var router: AppRouter

    func body(content: Content) -> some View {
        content.toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    router.push(.settings)
                } label: {
                    Image(systemName: "gearshape")
                }
            }
        }
    }
}

extension View {
    func withSettingsGear() -> some View {
        modifier(GearToolbarModifier())
    }
}
