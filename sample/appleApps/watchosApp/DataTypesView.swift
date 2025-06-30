import SwiftUI

struct DataTypesView : View {
    
    let navigateToHeartRate: () -> Void
    let navigateToHeight: () -> Void
    let navigateToSleep: () -> Void
    let navigateToSteps: () -> Void
    let navigateToWeight: () -> Void
    
    var body: some View {
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
        navigateToHeartRate: {},
        navigateToHeight: {},
        navigateToSleep: {},
        navigateToSteps: {},
        navigateToWeight: {},
    )
}
