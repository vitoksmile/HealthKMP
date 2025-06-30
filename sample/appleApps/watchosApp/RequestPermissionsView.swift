import SwiftUI

struct RequestPermissionsView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    let onAuthorized: () -> Void
    @State private var error: String? = nil
    
    var body: some View {
        Button("Request permissions") {
            Task {
                do {
                    let isAuthorized = try await health.requestAuthorization(
                        readTypes: readTypes,
                        writeTypes: writeTypes,
                    )
                    if (isAuthorized.boolValue) {
                        onAuthorized()
                    }
                } catch {
                    self.error = error.localizedDescription
                }
            }
        }
        
        if (error != nil) {
            Text("Failed to request permissions: \(error ?? "")")
                .foregroundColor(.red)
        }
    }
}

#Preview {
    RequestPermissionsView(onAuthorized: {})
}
