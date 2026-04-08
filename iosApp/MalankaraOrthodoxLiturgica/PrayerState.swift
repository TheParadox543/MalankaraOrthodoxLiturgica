//
//  PrayerState.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by Samuel Alex Koshy on 08/04/26.
//

import Foundation
import sharedKit

struct PrayerState {
    var elements: [PrayerUiElement] = []
    var isLoading: Bool = false
    var error: String? = nil
}
