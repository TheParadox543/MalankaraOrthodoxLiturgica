//
// Created by Sam Alex Koshy on 07/04/26.
//

import Foundation
import SwiftUI
import sharedKit

struct PrayerView: View {
    @StateObject private var viewModel = PrayerViewModel()

    var body: some View {
        Group {
            if viewModel.state.isLoading {
                ProgressView()
            } else if let error = viewModel.state.error {
                Text(error)
            } else {
                PrayerContentView(elements: viewModel.state.elements)
            }
        }
        .task {
            await viewModel.loadPrayer(fileName: "commonPrayers/kauma.json")
        }
    }
}
