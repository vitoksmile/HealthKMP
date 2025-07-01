import SwiftUI
import HealthKMP

enum Destination: Hashable {
    case bloodPressure
    case heartRate
    case height
    case steps
    case weight
}

struct ContentView: View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var navigation = NavigationPath()
    
    @State private var isLoading: Bool = true
    @State private var isAvailable: Bool = false
    @State private var isAuthorized: Bool = false
    @State private var error: String? = nil
    
    var body: some View {
        NavigationStack(path: $navigation) {
            ScrollView {
                VStack(spacing: 16) {
                    Text("Hello, this is HealthKMP app for watchOS")
                    
                    if (error != nil) {
                        Text(error ?? "")
                            .foregroundColor(.red)
                    }
                    
                    if (isLoading) {
                        ProgressView()
                    } else if (!isAvailable) {
                        Text("Apple Health Kit is not supported by this device")
                            .foregroundColor(.red)
                    } else if (!isAuthorized) {
                        RequestPermissionsView(onAuthorized: {
                            isAuthorized = true
                        })
                    } else {
                        DataTypesView(
                            navigateToBloodPressure: { navigation.append(Destination.bloodPressure) },
                            navigateToHeartRate: { navigation.append(Destination.heartRate) },
                            navigateToHeight: { navigation.append(Destination.height) },
                            navigateToSleep: {},
                            navigateToSteps: { navigation.append(Destination.steps) },
                            navigateToWeight: { navigation.append(Destination.weight) },
                        )
                    }
                }
            }
            .navigationTitle("HealthKMP")
            .navigationDestination(for: Destination.self) { destination in
                switch destination {
                case .bloodPressure:
                    BloodPressureView()
                case .heartRate:
                    HeartRateView()
                case .height:
                    HeightView()
                case .steps:
                    StepsView()
                case .weight:
                    WeightView()
                }
            }
            .onAppear {
                do {
                    isAvailable = try health.isAvailable()
                } catch {
                    self.error = error.localizedDescription
                }
                Task {
                    do {
                        isAuthorized = try await health.isAuthorized(
                            readTypes: readTypes,
                            writeTypes: writeTypes,
                        ).boolValue
                    } catch {
                        self.error = error.localizedDescription
                    }
                    isLoading = false
                }
            }
        }
    }
}

#Preview {
    ContentView()
}
