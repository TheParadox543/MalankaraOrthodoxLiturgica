import SwiftUI
import sharedKit

struct OnboardingComposeView: UIViewControllerRepresentable {
    let onNavigateToHome: () -> Void

    func makeUIViewController(context: Context) -> UIViewController {
        return Platform_iosKt.getOnboardingViewController(onNavigateToHome: onNavigateToHome)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
