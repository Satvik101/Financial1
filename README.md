# Financial1

Android finance planner app with multiple calculators (SIP, EMI, FD, CAGR, retirement, inflation, tax estimator, and more), history tracking, and goals.

## Prerequisites

- Android Studio (latest stable recommended)
- JDK 17
- Android SDK configured in local environment

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync complete.
4. Ensure `local.properties` points to your Android SDK.

## Build Debug APK

From project root:

```powershell
.\gradlew.bat assembleDebug
```

APK output path:

`app/build/outputs/apk/debug/app-debug.apk`

## Run Tests

```powershell
.\gradlew.bat test
```

## Project Structure

- `app/src/main/java/com/fincalc/app` - app source code
- `app/src/main/res` - layouts, drawables, menus, values
- `app/src/test` - unit tests

## Notes

- Build outputs and tool caches are excluded via `.gitignore`.
- If Gradle or SDK issues occur, run a clean sync from Android Studio.