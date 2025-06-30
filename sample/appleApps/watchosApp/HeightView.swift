import SwiftUI
import HealthKMP

struct HeightView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var isLoading: Bool = false
    @State private var heightAvg: Length? = nil
    @State private var heightMin: Length? = nil
    @State private var heightMax: Length? = nil
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputHeight: String = Int.random(in: 150..<200).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read height: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    VStack(alignment: .leading) {
                        Text("Height for last year:")
                        Text("- Average \(heightAvg?.inMeters.formatted() ?? "") meters")
                        Text("- Min \(heightMin?.inMeters.formatted() ?? "") meters")
                        Text("- Max \(heightMax?.inMeters.formatted() ?? "") meters")
                    }
                }
                
                Text("Write height")
                    .font(.caption)
                TextField("Enter height here...", text: $inputHeight)
                    .padding(.horizontal)
                    .onChange(of: inputHeight) { oldValue, newValue in
                        let filtered = newValue.filter { $0.isNumber }
                        if filtered != newValue {
                            self.inputHeight = filtered
                        }
                    }
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write height: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write height") {
                        writeHeight()
                    }
                }
            }
        }
        .navigationTitle("Height")
        .onAppear {
            loadHeight()
        }
    }
    
    private func loadHeight() {
        Task {
            isLoading = true
            do {
                let aggregatedHeight = try await health.aggregateHeight(
                    startTime: Calendar.current.date(byAdding: .year, value: -1, to: Date.now)!,
                    endTime: Date.now,
                )
                heightAvg = aggregatedHeight.avg
                heightMin = aggregatedHeight.min
                heightMax = aggregatedHeight.max
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeHeight() {
        let height = Double(inputHeight)
        if (height == nil) {
            writeError = "Can't convert height into Int32"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [HeightRecord(
                    time: Date.now.toKotlinInstant(),
                    height: Length.companion.meters(value: height! / 100.0),
                    metadata: generateManualEntryMetadata(),
                )])
                inputHeight = ""
                loadHeight()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    HeightView()
}
