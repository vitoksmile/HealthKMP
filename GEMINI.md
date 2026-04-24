# HealthKMP - Project Rules & Context

## 1. Core Architecture
- **Common-First Policy:** All business logic, health data interfaces, and models reside in the `health` module's `commonMain` source set.
- **Health Management:** The core interface is `HealthManager`, which defines methods for checking availability, requesting authorization, and reading/writing health data.
- **Factory Pattern:** Use `HealthManagerFactory` (an `expect`/`actual` class) to instantiate the appropriate platform-specific implementation of `HealthManager`.
- **Platform Implementations:**
    - **Android:** Utilizes `HealthConnectManager` (for Health Connect) and `GoogleFitManager` (for legacy Google Fit support).
    - **Apple (iOS/watchOS):** Utilizes `HealthKitManager` to interact with Apple HealthKit.
- **Sample Apps:**
    - `sample/composeApp`: Demonstrates usage with **Compose Multiplatform** for Android and iOS.
    - `sample/appleApps`: Demonstrates usage with **SwiftUI** for watchOS.

## 2. Coding Standards
- **Platform Purity:** No `java.*`, `android.*`, or `Foundation/UIKit` imports in `commonMain`. Use KMP-native alternatives like `kotlinx-datetime` and `kotlinx-coroutines`.
- **Async & Flow:** Use `kotlinx-coroutines` and `suspend` functions for asynchronous operations.
- **API Consistency:** Public methods in `HealthManager` should return `Result<T>` for robust error handling.
- **Data Integrity:** Record data models (e.g., `StepsRecord`, `WeightRecord`) should include `init` blocks with `require` statements to validate input ranges and logic.
- **Named Arguments:** **MANDATORY** in tests and highly encouraged for data model instantiation. This prevents parameter swaps (e.g., systolic vs. diastolic) and improves readability.
- **Language Style:** Standard software engineering English (American English) is used for all code, comments, and documentation (e.g., `isAuthorized`, `initialization`).

## 3. Testing & Verification
- **Test Locations:**
    - `commonTest`: Unit tests for data models and shared logic.
    - `androidHostTest`: Validation for Health Connect mapping (runs on JVM).
    - `appleTest`: Validation for HealthKit mapping (shared by iOS and watchOS).
- **Mapping Verification:** Any new `HealthRecord` must have corresponding mapping tests in both `androidHostTest` and `appleTest` to ensure data integrity during platform translation.
- **CI Enforcement:** GitHub Actions require all tests to pass (`:health:allTests`) before merging Pull Requests or publishing releases.

## 4. Tooling & Libraries
- **Kotlin:** Version 2.3.20.
- **Java:** Version 21.
- **Swift Interop:** Use **SKIE** for enhanced Swift interop, especially for `suspend` functions and `Flow`. Configuration is in `health/build.gradle.kts`.
- **API Stability:** The project uses the **Binary Compatibility Validator** to ensure public API stability.
- **Dependencies:** Always check `gradle/libs.versions.toml` before adding new dependencies. Use the version catalog for all declarations.

## 5. Interaction Guidelines
- **Discovery:** Always verify the existence of health data types and units in `health/src/commonMain/kotlin/com/viktormykhailiv/kmp/health/` before proposing changes.
- **Cross-Platform Awareness:** When modifying `commonMain` interfaces, ensure that implementations in `androidMain` and `appleMain` (shared by `iosMain` and `watchosMain`) are updated accordingly.
- **Build System:** If modifying the build configuration, be mindful of the custom XCFramework setup for Apple platforms and the specific dependencies for Android Health Connect/Google Fit.
- **Testing Requirements:** Every feature or bug fix must include tests. Use `./gradlew :health:allTests` to verify changes across all supported platforms.

## 6. Documentation
- **KDoc Requirement:** All public interfaces, classes, and methods in the `health` module must have KDoc documentation.
- **Content:** KDoc should explain the purpose, parameters, and return values of the component. For `HealthDataType` and `HealthRecord`, include unit information and valid ranges if applicable.
- **Multiplatform Clarity:** When documenting platform-specific implementations (e.g., `HealthConnectManager`, `HealthKitManager`), clearly state the underlying platform API being used.
