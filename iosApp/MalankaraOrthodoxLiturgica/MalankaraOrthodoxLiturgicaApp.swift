//
//  MalankaraOrthodoxLiturgicaApp.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by Samuel Alex Koshy on 05/04/26.
//

import SwiftUI
import sharedKit

@main
struct MalankaraOrthodoxLiturgicaApp: App {
    init() {
        SharedKit.shared.initialize()
        }

    var body: some Scene {
        WindowGroup {
            PrayerView()
        }
    }
}
