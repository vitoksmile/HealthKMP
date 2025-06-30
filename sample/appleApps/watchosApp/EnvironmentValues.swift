import SwiftUI
import HealthKMP

extension EnvironmentValues {
    @Entry var healthManager: SwiftHealthManager = SwiftHealthManager(manager: HealthManagerFactory().createManager())
    
    @Entry var readTypes: [HealthDataType] = [
        HealthDataTypeHeartRate(),
        HealthDataTypeSleep(),
        HealthDataTypeSteps(),
        HealthDataTypeWeight(),
    ]
    @Entry var writeTypes: [HealthDataType] = [
        HealthDataTypeHeartRate(),
        HealthDataTypeSleep(),
        HealthDataTypeSteps(),
        HealthDataTypeWeight(),
    ]
}
