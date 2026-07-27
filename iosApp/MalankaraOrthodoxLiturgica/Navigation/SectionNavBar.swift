import SwiftUI

/// Mirrors Android's `SectionNavBar` (`core/ui-common/.../navigation/SectionNavBar.kt`):
/// replaces the tab bar on Prayer/BibleChapter reading screens with
/// Previous / Generate-QR / Next, matching its 3-equal-width-item layout
/// and tinted background.
struct SectionNavBar: View {
    let hasPrev: Bool
    let hasNext: Bool
    let onPrev: () -> Void
    let onNext: () -> Void
    let onGenerateQr: () -> Void

    var body: some View {
        HStack(spacing: 0) {
            item(systemImage: "arrow.backward", label: "Previous", enabled: hasPrev, action: onPrev)
            item(systemImage: "qrcode", label: "Generate QR", enabled: true, action: onGenerateQr)
            item(systemImage: "arrow.forward", label: "Next", enabled: hasNext, action: onNext)
        }
        .padding(.top, 8)
        .padding(.bottom, 4)
        .background(Color.accentColor)
        .foregroundStyle(.white)
    }

    private func item(systemImage: String, label: String, enabled: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            VStack(spacing: 2) {
                Image(systemName: systemImage)
                    .font(.title3)
                Text(label)
                    .font(.caption2)
            }
            .frame(maxWidth: .infinity)
        }
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.3)
    }
}
