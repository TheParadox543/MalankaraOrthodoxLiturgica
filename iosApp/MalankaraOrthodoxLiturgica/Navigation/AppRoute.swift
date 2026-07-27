import Foundation

/// Mirrors `androidApp/.../ui/navigation/AppScreen.kt` one case per Android route object.
enum AppRoute: Hashable {
    case home
    case onboarding
    case prayNow
    case bible
    case bibleReader
    case calendar
    case qrScanner
    case index
    case settings
    case about
    case section(route: String)
    case prayer(route: String, scroll: Int)
    case song(route: String)
    case bibleBook(bookIndex: Int)
    case bibleChapter(bookIndex: Int, chapterIndex: Int)

    /// Swift enum cases can't declare default associated-value parameters
    /// (unlike Kotlin's `createRoute(prayerRoute: String, scroll: Int = 0)`).
    static func prayer(_ route: String) -> AppRoute {
        .prayer(route: route, scroll: 0)
    }
}
