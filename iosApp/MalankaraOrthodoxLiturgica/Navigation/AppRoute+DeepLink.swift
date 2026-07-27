import Foundation

/// Parses `app://liturgica/...` URLs into an `AppRoute`, mirroring every
/// `DEEP_LINK_PATTERN` in `AppScreen.kt` verbatim — `scheme="app"
/// host="liturgica"` (see `AndroidManifest.xml`'s intent filter), with the
/// screen name as the first path component, e.g. `app://liturgica/prayer/x/0`.
///
/// iOS previously registered its own `liturgica://...` scheme instead
/// (treating the screen name as the URL host rather than the first path
/// component) on the theory that it was "just where the scheme boundary
/// falls" with no functional difference. That's true for iOS's own
/// self-consistent handling, but not for anything that needs the literal
/// URL text to match across platforms — a QR code generated on iOS couldn't
/// be scanned by Android and vice versa. Since Android's format is already
/// shipped, iOS conforms to it instead of the other way around.
extension AppRoute {
    init?(url: URL) {
        guard url.scheme == "app", url.host == "liturgica" else { return nil }
        let pathComponents = url.pathComponents.filter { $0 != "/" }
        guard let routeType = pathComponents.first else { return nil }
        let args = Array(pathComponents.dropFirst())

        switch routeType {
        case "home" where args.isEmpty:
            self = .home
        case "bible" where args.isEmpty:
            self = .bible
        case "calendar" where args.isEmpty:
            self = .calendar
        case "index" where args.isEmpty:
            self = .index
        case "settings" where args.isEmpty:
            self = .settings
        case "about" where args.isEmpty:
            self = .about
        case "section":
            guard args.count == 1 else { return nil }
            self = .section(route: args[0])
        case "prayer":
            guard args.count == 2, let scroll = Int(args[1]) else { return nil }
            self = .prayer(route: args[0], scroll: scroll)
        case "bible":
            guard let bookIndex = Int(args[0]) else { return nil }
            if args.count == 1 {
                self = .bibleBook(bookIndex: bookIndex)
            } else if args.count == 2, let chapterIndex = Int(args[1]) {
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
