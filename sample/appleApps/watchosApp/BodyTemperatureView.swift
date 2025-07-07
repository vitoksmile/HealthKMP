import SwiftUI
import HealthKMP

struct BodyTemperatureView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var isLoading: Bool = false
    @State private var temperatureAvg: Temperature? = nil
    @State private var temperatureMin: Temperature? = nil
    @State private var temperatureMax: Temperature? = nil
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputTemperature: String = (Double(Int.random(in: 356..<399)) / 10.0).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read body temperature: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    VStack(alignment: .leading) {
                        Text("Body temperature for last month:")
                        Text("- Average \(temperatureAvg?.inCelsius.formatted() ?? "") C")
                        Text("- Min \(temperatureMin?.inCelsius.formatted() ?? "") C")
                        Text("- Max \(temperatureMax?.inCelsius.formatted() ?? "") C")
                    }
                }
                
                Text("Write body temperature")
                    .font(.caption)
                TextField("Enter body temperature here...", text: $inputTemperature)
                    .padding(.horizontal)
                    .onChange(of: inputTemperature) { oldValue, newValue in
                        let filtered = newValue.filter { $0.isNumber }
                        if filtered != newValue {
                            self.inputTemperature = filtered
                        }
                    }
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write body temperature: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write body temperature") {
                        writeBodyTemperature()
                    }
                }
            }
        }
        .navigationTitle("Body temperature")
        .onAppear {
            loadBodyTemperature()
        }
    }
    
    private func loadBodyTemperature() {
        Task {
            isLoading = true
            do {
                let aggregatedTemperature = try await health.aggregateBodyTemperature(
                    startTime: Calendar.current.date(byAdding: .year, value: -1, to: Date.now)!,
                    endTime: Date.now,
                )
                temperatureAvg = aggregatedTemperature.avg
                temperatureMin = aggregatedTemperature.min
                temperatureMax = aggregatedTemperature.max
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeBodyTemperature() {
        let temperature = Double(inputTemperature)
        if (temperature == nil) {
            writeError = "Can't convert temperature into Double"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [BodyTemperatureRecord(
                    time: Date.now.toKotlinInstant(),
                    temperature: Temperature.companion.celsius(value: temperature!),
                    measurementLocation: nil,
                    metadata: generateManualEntryMetadata(),
                )])
                inputTemperature = ""
                loadBodyTemperature()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    BodyTemperatureView()
}
