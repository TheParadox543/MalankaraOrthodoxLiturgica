import Testing
@testable import MalankaraOrthodoxLiturgica

@MainActor
struct SongPlayerViewModelTests {
    @Test func startsNotPlaying() async throws {
        let viewModel = SongPlayerViewModel()
        #expect(viewModel.isPlaying == false)
    }

    @Test func loadingMissingFileLeavesNotPlayingAndSetsError() async throws {
        let viewModel = SongPlayerViewModel()
        viewModel.load(filename: "does-not-exist.mp3")
        #expect(viewModel.isPlaying == false)
        #expect(viewModel.loadError != nil)
    }
}
