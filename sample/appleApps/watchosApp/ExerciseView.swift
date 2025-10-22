import SwiftUI
import HealthKMP

struct ExerciseView : View {
    
    @Environment(\.healthManager) private var health
    @Environment(\.readTypes) private var readTypes
    @Environment(\.writeTypes) private var writeTypes
    
    @State private var isLoading: Bool = false
    @State private var exercises: [ExerciseModel] = []
    @State private var readError: String? = nil
    
    @State private var isWriting: Bool = false
    private let exerciseTypes: [ExerciseType] = [
        ExerciseTypeBiking(),
        ExerciseTypeDancing(),
        ExerciseTypeGolf(),
        ExerciseTypeHiking(),
        ExerciseTypeRunning(),
        ExerciseTypeTennis(),
        ExerciseTypeYoga(),
    ]
    @State private var inputExerciseTypeIndex: Int = 0
    @State private var writeError: String? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                if (isLoading) {
                    ProgressView()
                } else if (readError != nil) {
                    Text("Failed to read exercise: \(readError ?? "")")
                        .foregroundColor(.red)
                } else {
                    Text("Exercises \(exercises.count)")
                    
                    LazyVStack {
                        ForEach(exercises) { exercise in
                            Text("- \(exercise.type)")
                        }
                    }
                }
                
                Picker(
                    selection: $inputExerciseTypeIndex,
                    label: Text("Exercise type")
                ) {
                    Text("Biking").tag(0)
                    Text("Dancing").tag(1)
                    Text("Golf").tag(2)
                    Text("Hiking").tag(3)
                    Text("Running").tag(4)
                    Text("Tennis").tag(5)
                    Text("Yoga").tag(6)
                }.pickerStyle(.navigationLink)

                if (isWriting) {
                    ProgressView()
                } else {
                    if (writeError != nil) {
                        Text("Failed to write exercise: \(writeError ?? "")")
                            .foregroundColor(.red)
                    }
                    
                    Button("Write \(exerciseTypes[inputExerciseTypeIndex])") {
                        writeExercise()
                    }
                }
            }
        }
        .navigationTitle("Exercise")
        .onAppear {
            inputExerciseTypeIndex = Int.random(in: 0..<exerciseTypes.count)
            loadExercise()
        }
    }
    
    private func loadExercise() {
        Task {
            isLoading = true
            do {
                let records = try await health.readExercise(
                    startTime: Calendar.current.date(byAdding: .day, value: -7, to: Date.now)!,
                    endTime: Date.now,
                )
                exercises = records.map { ExerciseModel(type: $0.exerciseType) }
            } catch {
                readError = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    private func writeExercise() {
        Task {
            isWriting = true
            do {
                let segmentsCount = 5
                let sampleInterval = 10
                let endTime = Date.now
                let startTime = Calendar.current.date(byAdding: .minute, value: -(segmentsCount * sampleInterval), to: endTime)!
                try await health.writeData(records: [ExerciseSessionRecord(
                    startTime: startTime.toKotlinInstant(),
                    endTime: endTime.toKotlinInstant(),
                    exerciseType: exerciseTypes[inputExerciseTypeIndex],
                    segments: (0..<segmentsCount).map { index in
                        ExerciseSegment(
                            startTime: Calendar.current.date(byAdding: .minute, value: index * sampleInterval, to: startTime)!.toKotlinInstant(),
                            endTime: Calendar.current.date(byAdding: .minute, value: index * sampleInterval + sampleInterval, to: startTime)!.toKotlinInstant(),
                            segmentType: ExerciseSegmentTypeOtherWorkout(),
                        )
                    },
                    laps: (0..<segmentsCount).map { index in
                        ExerciseLap(
                            startTime: Calendar.current.date(byAdding: .minute, value: index * sampleInterval, to: startTime)!.toKotlinInstant(),
                            endTime: Calendar.current.date(byAdding: .minute, value: index * sampleInterval + sampleInterval, to: startTime)!.toKotlinInstant(),
                            length: LengthKt.meters(Double(Int.random(in: 1..<100))),
                        )
                    },
                    exerciseRoute: nil,
                    metadata: generateManualEntryMetadata(),
                )])
                inputExerciseTypeIndex = Int.random(in: 0..<exerciseTypes.count)
                loadExercise()
            } catch {
                writeError = error.localizedDescription
            }
            isWriting = false
        }
    }
    
    struct ExerciseModel: Identifiable {
        let id = UUID()
        var type: ExerciseType
    }
}

#Preview {
    ExerciseView()
}
