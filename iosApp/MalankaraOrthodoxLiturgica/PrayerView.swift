//
// Created by Sam Alex Koshy on 07/04/26.
//

import Foundation
import SwiftUI
import sharedKit

struct PrayerView: View {
    @State private var elements: [PrayerUiElement] = []
    @State private var isLoading = true

    var body: some View {
        Group {
            if isLoading {
                ProgressView()
            } else {
                Text("Loaded \(elements.count) items")
            }
        }
        .task {
            // Ensure Koin is initialized
            SharedKit.shared.initialize()

            do {
                let api = SharedKit.shared.getPrayerApi()
                let result = try await api.loadPrayer(fileName: "commonPrayers/kauma.json")
                elements = result
            } catch {
                print("Error: \(error)")
            }

            isLoading = false
        }
    }
}
