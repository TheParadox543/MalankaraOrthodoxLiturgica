import SwiftUI
import AVFoundation

final class QrScanCoordinator: NSObject, AVCaptureMetadataOutputObjectsDelegate {
    var onScan: ((String) -> Void)?
    private var hasScanned = false

    func metadataOutput(
        _ output: AVCaptureMetadataOutput,
        didOutput metadataObjects: [AVMetadataObject],
        from connection: AVCaptureConnection
    ) {
        guard !hasScanned,
              let object = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
              object.type == .qr,
              let stringValue = object.stringValue else { return }
        hasScanned = true
        onScan?(stringValue)
    }

    func reset() {
        hasScanned = false
    }
}

struct QrCaptureView: UIViewControllerRepresentable {
    let onScan: (String) -> Void

    func makeCoordinator() -> QrScanCoordinator {
        let coordinator = QrScanCoordinator()
        coordinator.onScan = onScan
        return coordinator
    }

    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = UIViewController()
        let session = AVCaptureSession()

        guard let device = AVCaptureDevice.default(for: .video),
              let input = try? AVCaptureDeviceInput(device: device),
              session.canAddInput(input) else {
            return viewController
        }
        session.addInput(input)

        let output = AVCaptureMetadataOutput()
        guard session.canAddOutput(output) else { return viewController }
        session.addOutput(output)
        output.setMetadataObjectsDelegate(context.coordinator, queue: .main)
        output.metadataObjectTypes = [.qr]

        let previewLayer = AVCaptureVideoPreviewLayer(session: session)
        previewLayer.videoGravity = .resizeAspectFill
        previewLayer.frame = UIScreen.main.bounds
        viewController.view.layer.addSublayer(previewLayer)

        DispatchQueue.global(qos: .userInitiated).async {
            session.startRunning()
        }

        return viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct QrScannerView: View {
    @EnvironmentObject var router: AppRouter
    @State private var scanFailedMessage: String?

    var body: some View {
        ZStack(alignment: .bottom) {
            QrCaptureView { scanned in
                if !router.route(fromScannedString: scanned) {
                    scanFailedMessage = "Not a recognized code"
                }
            }
            .ignoresSafeArea()

            if let message = scanFailedMessage {
                Text(message)
                    .padding()
                    .background(.ultraThinMaterial, in: Capsule())
                    .padding(.bottom, 32)
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationTitle("Scan QR Code")
    }
}
