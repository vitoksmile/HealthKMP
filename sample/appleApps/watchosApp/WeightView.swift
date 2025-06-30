import SwiftUI
import HealthKMP

struct WeightView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var isLoading: Bool = false
    @State private var weightAvg: Mass? = nil
    @State private var weightMin: Mass? = nil
    @State private var weightMax: Mass? = nil
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputWeight: String = Int.random(in: 50..<100).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read weight: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    VStack(alignment: .leading) {
                        Text("Weight for last 30 days:")
                        Text("- Average \(weightAvg?.inKilograms.formatted() ?? "") kg")
                        Text("- Min \(weightMin?.inKilograms.formatted() ?? "") kg")
                        Text("- Max \(weightMax?.inKilograms.formatted() ?? "") kg")
                    }
                }
                
                Text("Write weight")
                    .font(.caption)
                TextField("Enter weight here...", text: $inputWeight)
                    .padding(.horizontal)
                    .onChange(of: inputWeight) { oldValue, newValue in
                        let filtered = newValue.filter { $0.isNumber }
                        if filtered != newValue {
                            self.inputWeight = filtered
                        }
                    }
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write weight: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write weight") {
                        writeWeight()
                    }
                }
            }
        }
        .navigationTitle("Weight")
        .onAppear {
            loadWeight()
        }
    }
    
    private func loadWeight() {
        Task {
            isLoading = true
            do {
                let aggregatedWeight = try await health.aggregateWeight(
                    startTime: Calendar.current.date(byAdding: .month, value: -1, to: Date.now)!,
                    endTime: Date.now,
                )
                weightAvg = aggregatedWeight.avg
                weightMin = aggregatedWeight.min
                weightMax = aggregatedWeight.max
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeWeight() {
        let weight = Double(inputWeight)
        if (weight == nil) {
            writeError = "Can't convert weight into Int32"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [WeightRecord(
                    time: Date.now.toKotlinInstant(),
                    weight: Mass.companion.kilograms(value: weight!),
                    metadata: generateManualEntryMetadata(),
                )])
                inputWeight = ""
                loadWeight()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    WeightView()
}
