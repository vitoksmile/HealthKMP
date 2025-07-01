import SwiftUI
import HealthKMP

extension EnvironmentValues {
    @Entry var healthManager: SwiftHealthManager = SwiftHealthManager(manager: HealthManagerFactory().createManager())
    
    @Entry var readTypes: [HealthDataType] = [
        HealthDataTypeBloodPressure(),
        HealthDataTypeHeartRate(),
        HealthDataTypeHeight(),
        HealthDataTypeSleep(),
        HealthDataTypeSteps(),
        HealthDataTypeWeight(),
    ]
    @Entry var writeTypes: [HealthDataType] = [
        HealthDataTypeBloodPressure(),
        HealthDataTypeHeartRate(),
        HealthDataTypeHeight(),
        HealthDataTypeSleep(),
        HealthDataTypeSteps(),
        HealthDataTypeWeight(),
    ]
}
