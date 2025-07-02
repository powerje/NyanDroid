# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NyanDroid is an Android live wallpaper featuring the famous Nyan Cat as "NyanDroid" flying through space. The project is written in Kotlin and targets Android devices with live wallpaper functionality.

## Architecture

- **Main Activity**: `NyanActivity.kt` - Fullscreen preview activity with audio playback
- **Live Wallpaper Service**: `NyanPaper.kt` - Wallpaper service implementation
- **Animation Core**: `NyanAnimation.kt` - Handles sprite animation and rendering logic
- **Custom View**: `NyanView.kt` - SurfaceView for displaying animations
- **Sprite Components**: Located in `sprites/` directory
  - `NyanDroid.kt` - Main character sprite
  - `Rainbow.kt` - Rainbow trail sprite
  - `Stars.kt` - Background star animations
- **Settings**: `NyanSettingsActivity.kt` and `NyanSettingsFragment.kt` - User preferences

The app uses a custom animation engine that renders sprites to a SurfaceView with frame-based animation cycles.

## Build and Development Commands

**Prerequisites**: Ensure Java 17 is installed and `JAVA_HOME` is set correctly:
```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.15/libexec/openjdk.jdk/Contents/Home
```

**Main Build Tasks**:
- `./gradlew build` - Full build including tests
- `./gradlew assembleDebug` - Build debug APK
- `./gradlew assembleRelease` - Build release APK (with ProGuard/R8)
- `./gradlew clean` - Clean build directory

**Installation and Testing**:
- `./gradlew installDebug` - Install debug build to connected device
- `./gradlew connectedDebugAndroidTest` - Run instrumentation tests on device
- `./gradlew testDebugUnitTest` - Run unit tests for debug build
- `./gradlew lint` - Run Android lint checks
- `./gradlew check` - Run all verification tasks

**Useful Development Tasks**:
- `./gradlew dependencies` - Show project dependencies
- `./gradlew signingReport` - Display signing configuration
- `./gradlew tasks` - List all available tasks

## Project Structure

- **Target SDK**: 35 (Android 15)
- **Min SDK**: 30 (Android 11)
- **Compile SDK**: 35
- **Build Tools**: 35.0.0
- **Gradle**: 8.11.1
- **Android Gradle Plugin**: 8.9.0
- **Kotlin**: 1.9.23
- **Dependencies**: androidx.appcompat, androidx.preference, Kotlin stdlib

## Key Technical Details

- Uses `SurfaceView` for efficient animation rendering
- Implements Android's `WallpaperService.Engine` for live wallpaper functionality
- Includes audio playback with looping background music (`dyan_loop.mp3`)
- Supports immersive fullscreen mode with transparent system bars
- Uses ProGuard/R8 for release builds with code shrinking and obfuscation
- Preference-based settings system for user customization