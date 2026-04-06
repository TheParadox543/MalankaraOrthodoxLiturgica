//
//  ContentView.swift
//  MalankaraOrthodoxLiturgica
//
//  Created by Samuel ALex Koshy on 05/04/26.
//

import SwiftUI
import sharedKit

struct ContentView: View {
    var body: some View {
        Text("Hello from iOS")
    }
    let result = FormatBibleRangeUseCase().invoke(range: <#T##ReferenceRange#>)
}

#Preview {
    ContentView()
}
