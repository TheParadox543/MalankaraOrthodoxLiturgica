//
//  ContentView.swift
//  iosApp
//
//  Created by Riya on 24/11/25.
//

import SwiftUI
import sharedKit

struct ContentView: View {
    var body: some View {
        VStack {
            Text(Greeting().greet())
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
