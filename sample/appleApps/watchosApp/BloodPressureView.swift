import SwiftUI
import HealthKMP

struct BloodPressureView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var isLoading: Bool = false
    @State private var systolicAvg: Pressure? = nil
    @State private var systolicMin: Pressure? = nil
    @State private var systolicMax: Pressure? = nil
    @State private var diastolicAvg: Pressure? = nil
    @State private var diastolicMin: Pressure? = nil
    @State private var diastolicMax: Pressure? = nil
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputSystolic: String = Int.random(in: 100..<140).formatted()
    @State private var inputDiastolic: String = Int.random(in: 70..<90).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read blood pressure: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    VStack(alignment: .leading) {
                        Text("Blood pressure for last year:")
                        Text("- Average \(systolicAvg?.inMillimetersOfMercury.formatted() ?? "")/\(diastolicAvg?.inMillimetersOfMercury.formatted() ?? "")")
                        Text("- Min \(systolicMin?.inMillimetersOfMercury.formatted() ?? "")/\(diastolicMin?.inMillimetersOfMercury.formatted() ?? "")")
                        Text("- Max \(systolicMax?.inMillimetersOfMercury.formatted() ?? "")/\(diastolicMax?.inMillimetersOfMercury.formatted() ?? "")")
                    }
                }
                
                Text("Write blood pressure")
                    .font(.caption)
                TextField("Enter systolic here...", text: $inputSystolic)
                    .padding(.horizontal)
                    .onChange(of: inputSystolic) { oldValue, newValue in
                        let filtered = newValue.filter { $0.isNumber }
                        if filtered != newValue {
                            self.inputSystolic = filtered
                        }
                    }
                TextField("Enter diastolic here...", text: $inputDiastolic)
                    .padding(.horizontal)
                    .onChange(of: inputDiastolic) { oldValue, newValue in
                        let filtered = newValue.filter { $0.isNumber }
                        if filtered != newValue {
                            self.inputDiastolic = filtered
                        }
                    }
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write blood pressure: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write blood pressure") {
                        writeBloodPressure()
                    }
                }
            }
        }
        .navigationTitle("Blood pressure")
        .onAppear {
            loadBloodPressure()
        }
    }
    
    private func loadBloodPressure() {
        Task {
            isLoading = true
            do {
                let aggregatedBloodPressure = try await health.aggregateBloodPressure(
                    startTime: Calendar.current.date(byAdding: .year, value: -1, to: Date.now)!,
                    endTime: Date.now,
                )
                systolicAvg = aggregatedBloodPressure.systolic.avg
                systolicMin = aggregatedBloodPressure.systolic.min
                systolicMax = aggregatedBloodPressure.systolic.max
                diastolicAvg = aggregatedBloodPressure.diastolic.avg
                diastolicMin = aggregatedBloodPressure.diastolic.min
                diastolicMax = aggregatedBloodPressure.diastolic.max
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeBloodPressure() {
        let systolic = Double(inputSystolic)
        if (systolic == nil) {
            writeError = "Can't convert systolic into Int32"
            return
        }
        let diastolic = Double(inputDiastolic)
        if (diastolic == nil) {
            writeError = "Can't convert diastolic into Int32"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [BloodPressureRecord(
                    time: Date.now.toKotlinInstant(),
                    systolic: Pressure.companion.millimetersOfMercury(value: systolic!),
                    diastolic: Pressure.companion.millimetersOfMercury(value: diastolic!),
                    bodyPosition: nil,
                    measurementLocation: nil,
                    metadata: generateManualEntryMetadata(),
                )])
                inputSystolic = ""
                inputDiastolic = ""
                loadBloodPressure()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    BloodPressureView()
}
