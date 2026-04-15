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
- **Language Style:** Standard software engineering English (American English) is used for all code, comments, and documentation (e.g., `isAuthorized`, `initialization`).

## 3. Tooling & Libraries
- **Kotlin:** Version 2.3.20.
- **Swift Interop:** Use **SKIE** for enhanced Swift interop, especially for `suspend` functions and `Flow`. Configuration is in `health/build.gradle.kts`.
- **API Stability:** The project uses the **Binary Compatibility Validator** to ensure public API stability.
- **Dependencies:** Always check `gradle/libs.versions.toml` before adding new dependencies. Use the version catalog for all declarations.

## 4. MCP Interaction Guidelines
- **Discovery:** Always verify the existence of health data types and units in `health/src/commonMain/kotlin/com/viktormykhailiv/kmp/health/` before proposing changes.
- **Cross-Platform Awareness:** When modifying `commonMain` interfaces, ensure that implementations in `androidMain` and `appleMain` (shared by `iosMain` and `watchosMain`) are updated accordingly.
- **Build System:** If modifying the build configuration, be mindful of the custom XCFramework setup for Apple platforms and the specific dependencies for Android Health Connect/Google Fit.
- **Testing:** Verify changes across all supported platforms (Android, iOS, watchOS) where possible.
