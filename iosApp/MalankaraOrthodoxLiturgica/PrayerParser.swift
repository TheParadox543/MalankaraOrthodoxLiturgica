//
//  PrayerParser.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by Paradox543 on 08/04/26.
//

import Foundation

final class PrayerParser {

    func parse(json: String) -> [PrayerElementDto] {
        let data = Data(json.utf8)

        do {
            return try JSONDecoder().decode([PrayerElementDto].self, from: data)
        } catch {
            print("Decoding error:", error)
            return []
        }
    }
}
