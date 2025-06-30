import SwiftUI
import HealthKMP

struct HeartRateView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var isLoading: Bool = false
    @State private var heartRateAvg: Int64 = 0
    @State private var heartRateMin: Int64 = 0
    @State private var heartRateMax: Int64 = 0
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputHeartRate: String = Int.random(in: 40..<300).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read heart rate: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    VStack(alignment: .leading) {
                        Text("Heart rate for last 14 days:")
                        Text("- Average \(heartRateAvg)")
                        Text("- Min \(heartRateMin)")
                        Text("- Max \(heartRateMax)")
                    }
                }
                
                Text("Write heart rate")
                    .font(.caption)
                TextField("Enter heart rate here...", text: $inputHeartRate)
                    .padding(.horizontal)
                    .onChange(of: inputHeartRate) { oldValue, newValue in
                        let filtered = newValue.filter { $0.isNumber }
                        if filtered != newValue {
                            self.inputHeartRate = filtered
                        }
                    }
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write heart rate: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write heart rate") {
                        writeHeartRate()
                    }
                }
            }
        }
        .navigationTitle("Heart rate")
        .onAppear {
            loadHeartRate()
        }
    }
    
    private func loadHeartRate() {
        Task {
            isLoading = true
            do {
                let aggregatedHeartRate = try await health.aggregateHeartRate(
                    startTime: Calendar.current.date(byAdding: .day, value: -14, to: Date.now)!,
                    endTime: Date.now,
                )
                heartRateAvg = aggregatedHeartRate.avg
                heartRateMin = aggregatedHeartRate.min
                heartRateMax = aggregatedHeartRate.max
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeHeartRate() {
        let heartRate = Int32(inputHeartRate)
        if (heartRate == nil) {
            writeError = "Can't convert heart rate into Int64"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [HeartRateRecord(
                    startTime: Date.now.toKotlinInstant(),
                    endTime: Date.now.toKotlinInstant(),
                    samples: [
                        HeartRateRecord.Sample(
                            time: Date.now.toKotlinInstant(),
                            beatsPerMinute: heartRate!,
                        ),
                    ],
                    metadata: generateManualEntryMetadata(),
                )])
                inputHeartRate = ""
                loadHeartRate()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    HeartRateView()
}
