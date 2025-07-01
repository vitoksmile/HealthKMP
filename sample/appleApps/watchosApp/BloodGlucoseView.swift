import SwiftUI
import HealthKMP

struct BloodGlucoseView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var isLoading: Bool = false
    @State private var bloodGlucoseAvg: BloodGlucose? = nil
    @State private var bloodGlucoseMin: BloodGlucose? = nil
    @State private var bloodGlucoseMax: BloodGlucose? = nil
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputBloodGlucose: String = Int.random(in: 20..<40).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read blood glucose: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    VStack(alignment: .leading) {
                        Text("Blood glucose for last 3 months:")
                        Text("- Average \(bloodGlucoseAvg?.inMillimolesPerLiter.formatted() ?? "") mmol/L")
                        Text("- Min \(bloodGlucoseMin?.inMillimolesPerLiter.formatted() ?? "") mmol/L")
                        Text("- Max \(bloodGlucoseMax?.inMillimolesPerLiter.formatted() ?? "") mmol/L")
                    }
                }
                
                Text("Write blood glucose")
                    .font(.caption)
                TextField("Enter blood glucose here...", text: $inputBloodGlucose)
                    .padding(.horizontal)
                    .onChange(of: inputBloodGlucose) { oldValue, newValue in
                        let filtered = newValue.filter { $0.isNumber }
                        if filtered != newValue {
                            self.inputBloodGlucose = filtered
                        }
                    }
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write blood glucose: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write blood glucose") {
                        writeBloodGlucose()
                    }
                }
            }
        }
        .navigationTitle("Blood glucose")
        .onAppear {
            loadBloodGlucose()
        }
    }
    
    private func loadBloodGlucose() {
        Task {
            isLoading = true
            do {
                let aggregatedBloodGlucose = try await health.aggregateBloodGlucose(
                    startTime: Calendar.current.date(byAdding: .month, value: -3, to: Date.now)!,
                    endTime: Date.now,
                )
                bloodGlucoseAvg = aggregatedBloodGlucose.avg
                bloodGlucoseMin = aggregatedBloodGlucose.min
                bloodGlucoseMax = aggregatedBloodGlucose.max
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeBloodGlucose() {
        let bloodGlucose = Double(inputBloodGlucose)
        if (bloodGlucose == nil) {
            writeError = "Can't convert blood glucose into Int32"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [BloodGlucoseRecord(
                    time: Date.now.toKotlinInstant(),
                    level: BloodGlucose.companion.millimolesPerLiter(value: bloodGlucose!),
                    specimenSource: nil,
                    mealType: nil,
                    relationToMeal: nil,
                    metadata: generateManualEntryMetadata(),
                )])
                inputBloodGlucose = ""
                loadBloodGlucose()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    BloodGlucoseView()
}
