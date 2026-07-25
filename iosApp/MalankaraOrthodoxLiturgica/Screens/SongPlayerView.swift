import SwiftUI
import Combine
import AVFAudio
import sharedKit

@MainActor
final class SongPlayerViewModel: NSObject, ObservableObject, AVAudioPlayerDelegate {
    @Published var isPlaying: Bool = false
    @Published var currentTime: TimeInterval = 0
    @Published var duration: TimeInterval = 0
    @Published var loadError: String?

    private var player: AVAudioPlayer?
    private var progressTimer: Timer?

    /// Song audio assets live in the app bundle under `Songs/`, matching how
    /// Android's `SongScreen` resolves `songFilename` against its own bundled
    /// assets via `node.filename`.
    func load(filename: String) {
        loadError = nil
        guard let url = Bundle.main.url(forResource: filename, withExtension: nil, subdirectory: "Songs") else {
            loadError = "Song file not found: \(filename)"
            return
        }
        do {
            let newPlayer = try AVAudioPlayer(contentsOf: url)
            newPlayer.delegate = self
            newPlayer.prepareToPlay()
            player = newPlayer
            duration = newPlayer.duration
            currentTime = 0
        } catch {
            loadError = error.localizedDescription
        }
    }

    func togglePlayback() {
        guard let player else { return }
        if player.isPlaying {
            player.pause()
            isPlaying = false
            stopProgressTimer()
        } else {
            player.play()
            isPlaying = true
            startProgressTimer()
        }
    }

    private func startProgressTimer() {
        stopProgressTimer()
        progressTimer = Timer.scheduledTimer(withTimeInterval: 0.25, repeats: true) { [weak self] _ in
            guard let self, let player = self.player else { return }
            self.currentTime = player.currentTime
        }
    }

    private func stopProgressTimer() {
        progressTimer?.invalidate()
        progressTimer = nil
    }

    nonisolated func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        Task { @MainActor in
            self.isPlaying = false
            self.currentTime = 0
            self.stopProgressTimer()
        }
    }

    deinit {
        progressTimer?.invalidate()
    }
}

struct SongPlayerView: View {
    let route: String
    @StateObject private var viewModel = SongPlayerViewModel()
    @EnvironmentObject var router: AppRouter

    var body: some View {
        VStack(spacing: 24) {
            if let error = viewModel.loadError {
                Text(error).foregroundStyle(.secondary)
            } else {
                Text(formatted(viewModel.currentTime)) + Text(" / ") + Text(formatted(viewModel.duration))
                Button {
                    viewModel.togglePlayback()
                } label: {
                    Image(systemName: viewModel.isPlaying ? "pause.circle.fill" : "play.circle.fill")
                        .resizable()
                        .frame(width: 64, height: 64)
                }
            }
        }
        .padding()
        .navigationBarTitleDisplayMode(.inline)
        .withSettingsGear()
        .onAppear {
            if let metadata = Platform_iosKt.getSongMetadata(route: route) {
                viewModel.load(filename: metadata.filename)
            } else {
                viewModel.loadError = "Song not found: \(route)"
            }
        }
    }

    private func formatted(_ time: TimeInterval) -> String {
        let minutes = Int(time) / 60
        let seconds = Int(time) % 60
        return String(format: "%d:%02d", minutes, seconds)
    }
}
