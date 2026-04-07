//
// Created by Sam Alex Koshy on 07/04/26.
//

import Foundation
import SwiftUI
import sharedKit

struct PrayerView: View {

    private let prayer = SharedKit().prayerApi.getSamplePrayer()

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(prayer.title)
                .font(.title)
                .fontWeight(.bold)

            ScrollView {
                Text(prayer.content)
                    .font(.body)
            }
        }
        .padding()
    }
}
