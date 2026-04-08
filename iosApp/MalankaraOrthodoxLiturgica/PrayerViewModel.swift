//
//  PrayerViewModel.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by FCI on 08/04/26.
//

import Foundation
import sharedKit
import Combine

@MainActor
class PrayerViewModel: ObservableObject {

    @Published private(set) var state = PrayerState()

    private var hasLoaded = false

    func loadPrayer(fileName: String) async {
        guard !hasLoaded else { return }
        hasLoaded = true

        state.isLoading = true
        state.error = nil

        do {
            let api = SharedKit.shared.getPrayerApi()
            let result = try await api.loadPrayer(fileName: fileName)
            state.elements = result
        } catch {
            state.error = error.localizedDescription
        }

        state.isLoading = false
    }
}
