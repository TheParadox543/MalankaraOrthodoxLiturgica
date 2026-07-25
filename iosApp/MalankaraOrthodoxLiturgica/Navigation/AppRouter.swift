import SwiftUI
import Combine
import sharedKit

enum AppTab: String, CaseIterable, Hashable {
    case home, prayNow, calendar, bible

    var title: String {
        switch self {
        case .home: return "Home"
        case .prayNow: return "Pray Now"
        case .calendar: return "Calendar"
        case .bible: return "Bible"
        }
    }

    var systemImage: String {
        switch self {
        case .home: return "house"
        case .prayNow: return "hands.sparkles"
        case .calendar: return "calendar"
        case .bible: return "book"
        }
    }
}

/// The Swift analogue of Android's `NavGraph.kt` — the only place `AppRoute`
/// values get created and pushed. Screens stay navigation-agnostic; they call
/// closures that end up calling `push(_:)` here, mirroring how only
/// `NavGraph.kt`, never a screen composable, calls `navController.navigate(...)`.
@MainActor
final class AppRouter: ObservableObject {
    @Published var selectedTab: AppTab = .home
    @Published var homePath: [AppRoute] = []
    @Published var prayNowPath: [AppRoute] = []
    @Published var calendarPath: [AppRoute] = []
    @Published var biblePath: [AppRoute] = []
    @Published var showOnboarding: Bool = false

    func push(_ route: AppRoute) {
        switch selectedTab {
        case .home: homePath.append(route)
        case .prayNow: prayNowPath.append(route)
        case .calendar: calendarPath.append(route)
        case .bible: biblePath.append(route)
        }
    }

    func popToRoot(_ tab: AppTab) {
        switch tab {
        case .home: homePath.removeAll()
        case .prayNow: prayNowPath.removeAll()
        case .calendar: calendarPath.removeAll()
        case .bible: biblePath.removeAll()
        }
    }

    func pathBinding(for tab: AppTab) -> Binding<[AppRoute]> {
        switch tab {
        case .home:
            return Binding(get: { self.homePath }, set: { self.homePath = $0 })
        case .prayNow:
            return Binding(get: { self.prayNowPath }, set: { self.prayNowPath = $0 })
        case .calendar:
            return Binding(get: { self.calendarPath }, set: { self.calendarPath = $0 })
        case .bible:
            return Binding(get: { self.biblePath }, set: { self.biblePath = $0 })
        }
    }

    /// Mirrors Android's bottom-nav tap behavior
    /// (`navController.navigate(route) { popBackStack(route, inclusive = true) }`):
    /// tapping the already-active tab clears its stack instead of doing nothing.
    func handleTabReselect(_ tappedTab: AppTab) {
        if tappedTab == selectedTab {
            popToRoot(tappedTab)
        } else {
            selectedTab = tappedTab
        }
    }

    func checkOnboardingStatus() {
        Platform_iosKt.getOnboardingCompleted { completed in
            self.showOnboarding = !completed.boolValue
        }
    }
}
