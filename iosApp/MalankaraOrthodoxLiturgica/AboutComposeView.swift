import SwiftUI
import sharedKit

struct AboutComposeView: UIViewControllerRepresentable {
    @ObservedObject var chromeState: ChromeState

    func makeUIViewController(context: Context) -> UIViewController {
        let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
        return Platform_iosKt.getAboutViewController(
            appVersion: version,
            onDeveloperContact: {
                if let url = URL(string: "mailto:dev@dquantix.com") {
                    UIApplication.shared.open(url)
                }
            },
            onExternalLinkClick: { link in
                if let url = URL(string: link) {
                    UIApplication.shared.open(url)
                }
            },
            onChromeStateChanged: { title, showFab in
                chromeState.update(title: title, showFab: showFab.boolValue)
            }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
