import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {

    init() {
        Main_iosKt.InitKoin()
    }

    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.all, edges: .bottom) // Compose has own keyboard handler
    }
}



