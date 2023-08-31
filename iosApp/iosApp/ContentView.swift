import UIKit
import SwiftUI
import core
import koin

struct ComposeView: UIViewControllerRepresentable {

    init() {
        Health_kmp_koinKt.start()
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



