//
//  PrayerElementUi.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by Paradox543 on 08/04/26.
//

import Foundation

indirect enum PrayerElementUi: Identifiable {

    case title(String)
    case heading(String)
    case subheading(String)
    case prose(String)
    case song(String)
    case subtext(String)
    case source(String)

    case button(
        link: String,
        label: String,
        replace: Bool
    )

    case link(file: String)
    case linkCollapsible(file: String)

    case collapsibleBlock(
        title: String,
        items: [PrayerElementUi],
        isExpanded: Bool
    )

    case dynamicSong(
        eventTitle: String,
        items: [PrayerElementUi]
    )

    case dynamicSongsBlock(
        items: [PrayerElementUi]
    )

    case alternativePrayersBlock(
        title: String,
        options: [AlternativeOptionUi],
        selectedIndex: Int
    )

    case error(String)

    var id: UUID {
        UUID()
    }
}

struct AlternativeOptionUi: Identifiable {
    let id = UUID()
    let label: String
    let items: [PrayerElementUi]
}
