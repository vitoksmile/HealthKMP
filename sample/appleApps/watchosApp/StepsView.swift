import SwiftUI
import HealthKMP

struct StepsView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var isLoading: Bool = false
    @State private var steps: Int64 = 0
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    @State private var inputSteps: String = Int.random(in: 1..<100).formatted()
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read steps: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    Text("\(steps) steps for last 7 days")
                }
                
                Text("Write steps")
                    .font(.caption)
                TextField("Enter steps here...", text: $inputSteps)
                    .padding(.horizontal)
                    .onChange(of: inputSteps) { oldValue, newValue in
                        let filtered = newValue.filter { $0.isNumber }
                        if filtered != newValue {
                            self.inputSteps = filtered
                        }
                    }
                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write steps: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write steps") {
                        writeSteps()
                    }
                }
            }
        }
        .navigationTitle("Steps")
        .onAppear {
            loadSteps()
        }
    }
    
    private func loadSteps() {
        Task {
            isLoading = true
            do {
                steps = try await health.aggregateSteps(
                    startTime: Calendar.current.date(byAdding: .day, value: -7, to: Date.now)!,
                    endTime: Date.now,
                ).count
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeSteps() {
        let stepsCount = Int32(inputSteps)
        if (stepsCount == nil) {
            writeError = "Can't convert steps into Int32"
            return
        }
        writeError = nil
        
        Task {
            isWriting = true
            do {
                try await health.writeData(records: [StepsRecord(
                    startTime: Date.now.toKotlinInstant(),
                    endTime: Date.now.toKotlinInstant(),
                    count: stepsCount!,
                    metadata: generateManualEntryMetadata(),
                )])
                inputSteps = ""
                loadSteps()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
}

#Preview {
    StepsView()
}
