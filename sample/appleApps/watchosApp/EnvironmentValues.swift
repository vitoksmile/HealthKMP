import SwiftUI
import HealthKMP

extension EnvironmentValues {
    @Entry var healthManager: SwiftHealthManager = SwiftHealthManager(manager: HealthManagerFactory().createManager())
    
    @Entry var readTypes: [HealthDataType] = [
        HealthDataTypeBloodGlucose(),
        HealthDataTypeBloodPressure(),
        HealthDataTypeBodyTemperature(),
        HealthDataTypeExercise(),
        HealthDataTypeHeartRate(),
        HealthDataTypeHeight(),
        HealthDataTypeSleep(),
        HealthDataTypeSteps(),
        HealthDataTypeWeight(),
    ]
    @Entry var writeTypes: [HealthDataType] = [
        HealthDataTypeBloodGlucose(),
        HealthDataTypeBloodPressure(),
        HealthDataTypeBodyTemperature(),
        HealthDataTypeExercise(),
        HealthDataTypeHeartRate(),
        HealthDataTypeHeight(),
        HealthDataTypeSleep(),
        HealthDataTypeSteps(),
        HealthDataTypeWeight(),
    ]
}
