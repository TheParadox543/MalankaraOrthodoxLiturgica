//
//  PrayerContentView.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by FCI on 11/04/26.
//

import SwiftUI
import sharedKit

struct PrayerContentView: View {
    let elements: [PrayerElementUi]

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 12) {
                ForEach(Array(elements.enumerated()), id: \.offset) { _, element in
                    PrayerElementView(element: element)
                }
            }
            .padding()
        }
    }
}
