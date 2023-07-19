# HealthKMM

Kotlin Multiplatform Mobile wrapper for HealthKit on iOS and ~~Google Fit and~~ Health Connect on Android.

> Google Fitness API is being deprecated and Health Connect the plugin will transition into the API as the Health Connect

The library supports:
- handling permissions to access health data using the `isAvailable`, `isAuthorized`, `requestAuthorization`, `revokeAuthorization` methods.

Note that for Android, the target phone **needs** to have ~~[Google Fit](https://www.google.com/fit/) or~~ [Health Connect](https://health.google/health-connect-android/) (which is currently in beta) installed and have access to the internet, otherwise this library will not work.

## Data Types
- STEPS
- WEIGHT