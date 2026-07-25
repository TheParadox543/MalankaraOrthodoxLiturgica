import Foundation
import FirebaseAnalytics
import sharedKit

class SwiftFirebaseAnalyticsLogger: NativeAnalyticsLogger {
    func logEvent(name: String, params: [String: Any]?) {
        Analytics.logEvent(name, parameters: params)
    }
}
