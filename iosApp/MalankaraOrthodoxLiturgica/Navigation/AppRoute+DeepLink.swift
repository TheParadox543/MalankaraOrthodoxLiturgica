import Foundation

/// Parses `liturgica://...` URLs into an `AppRoute`, mirroring every
/// `DEEP_LINK_PATTERN` in `AppScreen.kt` verbatim. Android's custom scheme is
/// `app://liturgica/...`; iOS registers `liturgica://...` directly (see
/// `iosApp/MalankaraOrthodoxLiturgica-Info.plist`'s `CFBundleURLTypes`) since
/// a custom URL scheme's "host" plays the same role Android's URI authority
/// does — there is no functional difference, just where the scheme boundary
/// falls.
extension AppRoute {
    init?(url: URL) {
        guard let host = url.host, !host.isEmpty else { return nil }
        let pathComponents = url.pathComponents.filter { $0 != "/" }

        switch host {
        case "home" where pathComponents.isEmpty:
            self = .home
        case "bible" where pathComponents.isEmpty:
            self = .bible
        case "calendar" where pathComponents.isEmpty:
            self = .calendar
        case "index" where pathComponents.isEmpty:
            self = .index
        case "settings" where pathComponents.isEmpty:
            self = .settings
        case "about" where pathComponents.isEmpty:
            self = .about
        case "section":
            guard pathComponents.count == 1 else { return nil }
            self = .section(route: pathComponents[0])
        case "prayer":
            guard pathComponents.count == 2, let scroll = Int(pathComponents[1]) else { return nil }
            self = .prayer(route: pathComponents[0], scroll: scroll)
        case "bible":
            guard let bookIndex = Int(pathComponents[0]) else { return nil }
            if pathComponents.count == 1 {
                self = .bibleBook(bookIndex: bookIndex)
            } else if pathComponents.count == 2, let chapterIndex = Int(pathComponents[1]) {
                self = .bibleChapter(bookIndex: bookIndex, chapterIndex: chapterIndex)
            } else {
                return nil
            }
        default:
            // onboarding, prayNow, bibleReader, qrScanner, song: no Android
            // deep link exists for these either (AppScreen.kt's `deepLink`
            // constructor param defaults to null for all five).
            return nil
        }
    }
}
