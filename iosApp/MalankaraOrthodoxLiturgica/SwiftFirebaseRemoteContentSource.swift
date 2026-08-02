import Foundation
import FirebaseStorage
import sharedKit

class SwiftFirebaseRemoteContentSource: NativeRemoteContentSource {
    private let storage = Storage.storage().reference()
    private let maxDownloadSize: Int64 = 20 * 1024 * 1024 // 20MB

    func fetchRootManifest() async throws -> String {
        return try await downloadString(path: "manifest.json")
    }

    func fetchDomainManifest(path: String) async throws -> String {
        return try await downloadString(path: path)
    }

    func downloadFile(path: String) async throws -> String {
        return try await downloadString(path: path)
    }

    private func downloadString(path: String) async throws -> String {
        let ref = storage.child(path)
        let data = try await withCheckedThrowingContinuation { continuation in
            ref.getData(maxSize: maxDownloadSize) { data, error in
                if let error = error {
                    continuation.resume(throwing: error)
                } else if let data = data {
                    continuation.resume(returning: data)
                }
            }
        }
        guard let result = String(data: data, encoding: .utf8) else {
            throw NSError(domain: "SwiftFirebaseRemoteContentSource", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to decode UTF8 string"])
        }
        return result
    }
}
