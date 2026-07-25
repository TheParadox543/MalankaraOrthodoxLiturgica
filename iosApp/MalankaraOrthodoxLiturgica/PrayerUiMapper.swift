//
//  PrayerUiMapper.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by Paradox543 on 08/04/26.
//

import Foundation

final class PrayerUiMapper {

    func map(dto: [PrayerElementDto]) -> [PrayerElementUi] {
        dto.map { mapElement($0) }
    }

    private func mapElement(_ element: PrayerElementDto) -> PrayerElementUi {

        switch element {

        // MARK: - Simple

        case .title(let content):
            return .title(content)

        case .heading(let content):
            return .heading(content)

        case .subheading(let content):
            return .subheading(content)

        case .prose(let content):
            return .prose(content)

        case .song(let content):
            return .song(content)

        case .subtext(let content):
            return .subtext(content)

        case .source(let content):
            return .source(content)

        // MARK: - Button

        case .button(let link, let label, let replace):
            return .button(
                link: link,
                label: label ?? "Open",
                replace: replace
            )

        // MARK: - Links

        case .link(let file):
            return .link(file: file)

        case .linkCollapsible(let file):
            return .linkCollapsible(file: file)

        // MARK: - Collapsible

        case .collapsibleBlock(let title, let items):
            return .collapsibleBlock(
                title: title,
                items: map(dto: items),
                isExpanded: false
            )

        // MARK: - Dynamic Song

        case .dynamicSong(_, let eventTitle, _, let items):
            return .dynamicSong(
                eventTitle: eventTitle,
                items: map(dto: items)
            )

        // MARK: - Dynamic Songs Block (IMPORTANT)

        case .dynamicSongsBlock(_, let items, let defaultContent):

            // Convert all songs to UI
            let mappedSongs: [PrayerElementUi] = items.map {
                mapElement(
                    .dynamicSong(
                        eventKey: $0.eventKey,
                        eventTitle: $0.eventTitle,
                        timeKey: $0.timeKey,
                        items: $0.items
                    )
                )
            }

            // Fallback if empty
            if mappedSongs.isEmpty, let fallback = defaultContent {
                let fallbackUi = mapElement(
                    .dynamicSong(
                        eventKey: fallback.eventKey,
                        eventTitle: fallback.eventTitle,
                        timeKey: fallback.timeKey,
                        items: fallback.items
                    )
                )

                return .dynamicSongsBlock(items: [fallbackUi])
            }

            return .dynamicSongsBlock(items: mappedSongs)

        // MARK: - Alternative Option (handled inside block)

        case .alternativeOption:
            // Should not be rendered directly
            return .error("Invalid standalone alternative option")

        // MARK: - Alternative Prayers Block (IMPORTANT)

        case .alternativePrayersBlock(let title, let options):

            let mappedOptions = options.map {
                AlternativeOptionUi(
                    label: $0.label,
                    items: map(dto: $0.items)
                )
            }

            return .alternativePrayersBlock(
                title: title,
                options: mappedOptions,
                selectedIndex: 0
            )

        // MARK: - Error

        case .error(let content):
            return .error(content)
        }
    }
}
