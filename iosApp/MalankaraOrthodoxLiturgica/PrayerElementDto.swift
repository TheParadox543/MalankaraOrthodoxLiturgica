//
//  PrayerElementDto.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by Paradox543 on 08/04/26.
//

import Foundation

enum PrayerElementDto: Decodable {

    case title(String)
    case heading(String)
    case subheading(String)
    case prose(String)
    case song(String)
    case subtext(String)
    case source(String)

    case button(link: String, label: String?, replace: Bool)

    case link(file: String)
    case linkCollapsible(file: String)

    case collapsibleBlock(title: String, items: [PrayerElementDto])

    case dynamicSong(
        eventKey: String,
        eventTitle: String,
        timeKey: String,
        items: [PrayerElementDto]
    )

    case dynamicSongsBlock(
        timeKey: String,
        items: [DynamicSongDto],
        defaultContent: DynamicSongDto?
    )

    case alternativeOption(label: String, items: [PrayerElementDto])

    case alternativePrayersBlock(
        title: String,
        options: [AlternativeOptionDto]
    )

    case error(String)

    // MARK: - Nested DTOs

    struct DynamicSongDto: Decodable {
        let eventKey: String
        let eventTitle: String
        let timeKey: String
        let items: [PrayerElementDto]
    }

    struct AlternativeOptionDto: Decodable {
        let label: String
        let items: [PrayerElementDto]
    }

    // MARK: - Coding

    enum CodingKeys: String, CodingKey {
        case type
        case content
        case link
        case label
        case replace
        case file
        case title
        case items
        case eventKey
        case eventTitle
        case timeKey
        case options
        case defaultContent
    }

    enum ElementType: String, Decodable {
        case title
        case heading
        case subheading
        case prose
        case song
        case subtext
        case source
        case button
        case link
        case linkCollapsible = "link-collapsible"
        case collapsibleBlock = "collapsible-block"
        case dynamicSong = "dynamic-song"
        case dynamicSongsBlock = "dynamic-songs-block"
        case alternativeOption = "alternative-option"
        case alternativePrayersBlock = "alternative-prayers-block"
        case error
    }

    init(from decoder: Decoder) throws {

        let container = try decoder.container(keyedBy: CodingKeys.self)
        let type = try container.decode(ElementType.self, forKey: .type)

        switch type {

        case .title:
            self = .title(try container.decode(String.self, forKey: .content))

        case .heading:
            self = .heading(try container.decode(String.self, forKey: .content))

        case .subheading:
            self = .subheading(try container.decode(String.self, forKey: .content))

        case .prose:
            self = .prose(try container.decode(String.self, forKey: .content))

        case .song:
            self = .song(try container.decode(String.self, forKey: .content))

        case .subtext:
            self = .subtext(try container.decode(String.self, forKey: .content))

        case .source:
            self = .source(try container.decode(String.self, forKey: .content))

        case .button:
            self = .button(
                link: try container.decode(String.self, forKey: .link),
                label: try container.decodeIfPresent(String.self, forKey: .label),
                replace: try container.decode(Bool.self, forKey: .replace)
            )

        case .link:
            self = .link(file: try container.decode(String.self, forKey: .file))

        case .linkCollapsible:
            self = .linkCollapsible(file: try container.decode(String.self, forKey: .file))

        case .collapsibleBlock:
            self = .collapsibleBlock(
                title: try container.decode(String.self, forKey: .title),
                items: try container.decode([PrayerElementDto].self, forKey: .items)
            )

        case .dynamicSong:
            self = .dynamicSong(
                eventKey: try container.decode(String.self, forKey: .eventKey),
                eventTitle: try container.decode(String.self, forKey: .eventTitle),
                timeKey: try container.decode(String.self, forKey: .timeKey),
                items: try container.decode([PrayerElementDto].self, forKey: .items)
            )

        case .dynamicSongsBlock:
            self = .dynamicSongsBlock(
                timeKey: try container.decode(String.self, forKey: .timeKey),
                items: try container.decode([DynamicSongDto].self, forKey: .items),
                defaultContent: try container.decodeIfPresent(DynamicSongDto.self, forKey: .defaultContent)
            )

        case .alternativeOption:
            self = .alternativeOption(
                label: try container.decode(String.self, forKey: .label),
                items: try container.decode([PrayerElementDto].self, forKey: .items)
            )

        case .alternativePrayersBlock:
            self = .alternativePrayersBlock(
                title: try container.decode(String.self, forKey: .title),
                options: try container.decode([AlternativeOptionDto].self, forKey: .options)
            )

        case .error:
            self = .error(try container.decode(String.self, forKey: .content))
        }
    }
}
