// Temporary stand-ins, each replaced by its real implementation in a later task:
// CalendarTabRootView/BibleTabRootView (Task 8), BibleReaderPushedView/
// BibleBookPushedView/BibleChapterPushedView (Task 8), SettingsPushedView/
// AboutPushedView/OnboardingComposeView (Task 11), SongPushedView (Task 13),
// QrScannerPushedView (Task 14).
import SwiftUI

struct CalendarTabRootView: View { var body: some View { Text("Calendar") } }
struct BibleTabRootView: View { var body: some View { Text("Bible") } }
struct BibleReaderPushedView: View { var body: some View { Text("Bible Reader") } }
struct BibleBookPushedView: View { let bookIndex: Int; var body: some View { Text("Bible Book \(bookIndex)") } }
struct BibleChapterPushedView: View {
    let bookIndex: Int
    let chapterIndex: Int
    var body: some View { Text("Bible Chapter \(bookIndex)/\(chapterIndex)") }
}
struct SettingsPushedView: View { var body: some View { Text("Settings") } }
struct AboutPushedView: View { var body: some View { Text("About") } }
struct OnboardingComposeView: View {
    let onNavigateToHome: () -> Void
    var body: some View { Button("Skip Onboarding", action: onNavigateToHome) }
}
struct SongPushedView: View { let route: String; var body: some View { Text("Song \(route)") } }
struct QrScannerPushedView: View { var body: some View { Text("QR Scanner") } }
