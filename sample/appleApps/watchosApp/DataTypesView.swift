import SwiftUI

struct DataTypesView : View {
    
    let navigateToBloodGlucose: () -> Void
    let navigateToBloodPressure: () -> Void
    let navigateToBodyTemperature: () -> Void
    let navigateToExercise: () -> Void
    let navigateToHeartRate: () -> Void
    let navigateToHeight: () -> Void
    let navigateToSleep: () -> Void
    let navigateToSteps: () -> Void
    let navigateToWeight: () -> Void
    
    var body: some View {
        Button("Blood glucose") {
            navigateToBloodGlucose()
        }
        
        Button("Blood pressure") {
            navigateToBloodPressure()
        }
        
        Button("Body temperature") {
            navigateToBodyTemperature()
        }
        
        Button("Exercise") {
            navigateToExercise()
        }
        
        Button("Heart rate") {
            navigateToHeartRate()
        }
        
        Button("Height") {
            navigateToHeight()
        }
        
//        Button("Sleep") {
//            navigateToSleep()
//        }
        
        Button("Steps") {
            navigateToSteps()
        }
        
        Button("Weight") {
            navigateToWeight()
        }
    }
}

#Preview {
    DataTypesView(
        navigateToBloodGlucose: {},
        navigateToBloodPressure: {},
        navigateToBodyTemperature: {},
        navigateToExercise: {},
        navigateToHeartRate: {},
        navigateToHeight: {},
        navigateToSleep: {},
        navigateToSteps: {},
        navigateToWeight: {},
    )
}
