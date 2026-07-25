//
// Created by Sam Alex Koshy on 07/04/26.
//

import SwiftUI
import sharedKit

struct PrayerView: View {
    var body: some View {
        ComposeView(
            fileName: "commonPrayers/kauma.json",
            onPrayerButtonClick: { link, replace in
                print("Button clicked: \(link), replace: \(replace)")
            }
        )
    }
}
