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

    // 👇 Add these
    private let parser = PrayerParser()
    private let mapper = PrayerUiMapper()

    func loadPrayer(fileName: String) async {
        guard !hasLoaded else { return }
        hasLoaded = true

        state.isLoading = true
        state.error = nil

        do {
            let api = SharedKit.shared.getPrayerApi()

            // 1. Get JSON string from Kotlin
            let json = try await api.loadPrayer(fileName: fileName)

            // 2. Decode JSON → DTO
            let dto = parser.parse(json: json)

            // 3. Map DTO → UI model
            let uiElements = mapper.map(dto: dto)

            // 4. Assign to state
            state.elements = uiElements

        } catch {
            state.error = error.localizedDescription
        }

        state.isLoading = false
    }
}
