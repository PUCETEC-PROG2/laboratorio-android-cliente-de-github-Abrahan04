## Copilot / AI agent instructions — laboratorio-android-cliente-de-github-ProgramacionVPA

These notes give an AI coding agent the minimal, actionable knowledge to be productive in this Android Gradle project.

1) Project type & important files
- Android application using Gradle with a Version Catalog. Key observed file:
  - `gradle/libs.versions.toml` — defines AGP (8.13.0), Kotlin (2.0.21) and common libs.
  - Plugin ids referenced in the catalog: `com.android.application`, `org.jetbrains.kotlin.android`.
  - Typical code lives under module folders (look for `app/src/main/java` or `app/src/main/kotlin`).

2) Build / test / debug workflows (concrete commands for Windows PowerShell)
- Use the Gradle wrapper in the repo root. From the project root run:
  - Build debug APK: `.\\gradlew assembleDebug`
  - Run unit tests: `.\\gradlew test`
  - Run instrumentation tests (if device attached / CI): `.\\gradlew connectedDebugAndroidTest` or `.\\gradlew connectedAndroidTest`
- Open the project in Android Studio for IDE-driven builds, run configurations, and debugger.
- Common debugging: after installing a build, use `adb logcat` (external command) to inspect runtime logs.

3) Dependency & plugin conventions to follow
- Centralized versions via `gradle/libs.versions.toml` (version catalog). When modifying or adding deps, prefer adding entries to that file and referencing them via the catalog.
- Do not hardcode library versions directly in module build files; follow the catalog and plugin mapping.

4) What the agent should look for in code changes
- If touching Gradle/plugin versions, verify compatibility with Kotlin `2.0.21` and AGP `8.13.0` (see `gradle/libs.versions.toml`).
- Look for module-level `build.gradle(.kts)` files to understand applied plugins (`com.android.application` vs `com.android.library`) before changing module types.

5) Architecture & areas to inspect when asked to modify features
- Start with the app module (likely `app/`) and `app/src/main` for UI and entrypoint code.
- Check `gradle/` for shared build configuration and `libs.versions.toml` for dependencies.
- For network, authentication, or storage changes: search for packages named `data`, `network`, `repository`, or `ui` under `src/main`.

6) CI / environment notes
- The project uses the Gradle wrapper; CI should invoke the wrapper (not system gradle) to ensure consistent AGP/Kotlin.
- On Windows use `.\gradlew` (PowerShell). On Unix use `./gradlew`.

7) Examples (concrete snippets found)
- Version catalog excerpt: `gradle/libs.versions.toml` contains:
  - `android-application = { id = "com.android.application", version.ref = "agp" }`
  - `kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }`

8) Constraints and caveats
- Don't assume module names — confirm by listing the project root for `settings.gradle(.kts)` to see declared modules before moving files.
- When updating AGP/Kotlin, run a local build and unit tests, since compatibility issues are common.

If any section is unclear or you'd like more specifics (examples of module layout, test commands, or CI steps), tell me which area to expand and I'll update this file.
