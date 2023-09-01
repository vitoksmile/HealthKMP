import UIKit
import SwiftUI
import HealthKMP
import HealthKMPKoin
import HealthKMPSample

struct ComposeView: UIViewControllerRepresentable {

    init() {
        HealthKMPKoin().start()
    }

    func makeUIViewController(context: Context) -> UIViewController {
        HealthKMPSample().MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.all, edges: .bottom) // Compose has own keyboard handler
    }
}



