# Mac Finalization Steps for iOS Firebase Integration

These steps should be executed on a Mac with Xcode installed to finalize the Firebase Analytics and Crashlytics setup.

## 1. Add Firebase Swift Packages
Since the project currently lacks the Firebase SDK dependencies, they must be added to the `.xcodeproj`.

**Repository URL**: `https://github.com/firebase/firebase-ios-sdk`
**Packages to add**:
- `FirebaseAnalytics`
- `FirebaseCrashlytics`

> [!NOTE]
> If using Claude Code or a CLI-based agent, this is best done via the Xcode UI:
> `File -> Add Packages...` -> Paste URL -> Select the two libraries -> Add to `MalankaraOrthodoxLiturgica` target.

## 2. Verify File Locations
Ensure the following files are correctly detected by the build system:
- `iosApp/MalankaraOrthodoxLiturgica/GoogleService-Info.plist` (Must be inside the app target folder).
- `iosApp/MalankaraOrthodoxLiturgica/Analytics/SwiftFirebaseAnalyticsLogger.swift`

## 3. Trigger KMP Framework Build
Run the following command from the project root to ensure the shared Kotlin code is compiled and bridged for iOS:
```bash
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

## 4. Perform a Full Xcode Build
Validate that the Swift code compiles and the Firebase Crashlytics script executes correctly:
```bash
cd iosApp
xcodebuild -project MalankaraOrthodoxLiturgica.xcodeproj \
           -scheme MalankaraOrthodoxLiturgica \
           -configuration Debug \
           -destination 'generic/platform=iOS' \
           build
```

## 5. Verify Crashlytics Setup
Check the build logs for the "Firebase Crashlytics" build phase. It should attempt to run the `run` script located in the SPM checkout directory:
`"${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"`

## 6. Verification Checklist
- [ ] App launches without crashing (Firebase initialized).
- [ ] Analytics events are visible in Console (after running on a device/simulator).
- [ ] dSYMs are uploaded during build (check build log for Crashlytics output).
