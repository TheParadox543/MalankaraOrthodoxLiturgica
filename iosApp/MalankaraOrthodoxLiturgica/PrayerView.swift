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
                ScrollView {
                    LazyVStack(alignment: .leading, spacing: 12) {
                        ForEach(Array(viewModel.state.elements.enumerated()), id: \.offset) { _, element in
                            PrayerElementView(element: element)
                        }
                    }
                    .padding()
                }
            }
        }
        .task {
            await viewModel.loadPrayer(fileName: "commonPrayers/kauma.json")
        }
    }
}
