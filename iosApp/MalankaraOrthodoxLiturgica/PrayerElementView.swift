//
//  PrayerElementView.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by Paradox543 on 08/04/26.
//

import SwiftUI

struct PrayerElementView: View {

    let element: PrayerElementUi

    var body: some View {

        switch element {

        // MARK: - Simple

        case .title(let text):
            Text(text)
                .font(.title)
                .fontWeight(.bold)

        case .heading(let text):
            Text(text)
                .font(.headline)

        case .subheading(let text):
            Text(text)
                .font(.subheadline)
                .fontWeight(.semibold)

        case .prose(let text):
            Text(text)
                .font(.body)

        case .song(let text):
            Text(text)
                .italic()

        case .subtext(let text):
            Text(text)
                .font(.footnote)
                .foregroundColor(.gray)

        case .source(let text):
            Text(text)
                .font(.caption)
                .foregroundColor(.secondary)

        // MARK: - Button

        case .button(let link, let label, _):
            Button(label) {
                print("Navigate to \(link)")
            }

        // MARK: - Links

        case .link(let file):
            Text("Open: \(file)")
                .foregroundColor(.blue)

        case .linkCollapsible(let file):
            DisclosureGroup("Open") {
                Text(file)
            }

        // MARK: - Collapsible Block

        case .collapsibleBlock(let title, let items, _):
            DisclosureGroup(title) {
                ForEach(items) { item in
                    PrayerElementView(element: item)
                }
            }

        // MARK: - Dynamic Song

        case .dynamicSong(let eventTitle, let items):
            VStack(alignment: .leading, spacing: 6) {
                Text(eventTitle)
                    .font(.headline)

                ForEach(items) { item in
                    PrayerElementView(element: item)
                }
            }

        // MARK: - Dynamic Songs Block

        case .dynamicSongsBlock(let items):
            VStack(alignment: .leading, spacing: 8) {
                ForEach(items) { item in
                    PrayerElementView(element: item)
                }
            }

        // MARK: - Alternative Prayers

        case .alternativePrayersBlock(let title, let options, _):
            VStack(alignment: .leading, spacing: 8) {

                Text(title)
                    .font(.headline)

                ForEach(options) { option in
                    VStack(alignment: .leading, spacing: 4) {

                        Text(option.label)
                            .font(.subheadline)
                            .bold()

                        ForEach(option.items) { item in
                            PrayerElementView(element: item)
                        }
                    }
                }
            }

        // MARK: - Error

        case .error(let text):
            Text(text)
                .foregroundColor(.red)
        }
    }
}
