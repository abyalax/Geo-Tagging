# KMP Commands Knowledge (from docs/command.md)

## Overview

Documentation of essential commands for **Kotlin Multiplatform** development, debugging, deployment, and app distribution for **Geo-Tagging** application with **Clean Architecture**.

## Prerequisites

- Android SDK installed
- Android Emulator configured (Medium_Phone_API_36.1)
- ADB available in PATH
- Gradle wrapper configured

## 🐛 Debug Commands (KMP)

### Clean Build
```bash
./gradlew clean
```
Remove all build artifacts and start fresh.

### Compile Check (Common Code)
```bash
./gradlew compileDebugKotlinAndroid
./gradlew compileKotlinMetadata
```
Check for compilation errors in both common and Android code.

### Build Debug APK
```bash
./gradlew composeApp:assembleDebug
```
Build debug APK for testing.

### Build All Platforms
```bash
./gradlew build
```
Build for all configured platforms (Android, iOS if configured).

## 🚀 Deploy Commands (KMP)

### Install to Emulator (Android)
```bash
./gradlew composeApp:installDebug
```
Build and install debug APK to connected device/emulator.

### Run Android Tests
```bash
./gradlew composeApp:check
```
Run all Android tests including unit and integration tests.

### Build Release APK
```bash
./gradlew composeApp:assembleRelease
```
Build production-ready APK for Android.

### Build Android App Bundle
```bash
./gradlew composeApp:bundleRelease
```
Create Android App Bundle for Play Store distribution.

## 📦 Export & Distribution Commands (KMP)

### Generate Release APK
```bash
./gradlew composeApp:assembleRelease
```
Build production-ready APK.

### Generate Signed APK
```bash
./gradlew composeApp:assembleRelease -Pandroid.injected.signing.file.name=keystore.jks -Pandroid.injected.signing.key.alias=release -Pandroid.injected.signing.key.password=yourpassword -Pandroid.injected.signing.store.password=yourstorepassword
```
Build signed APK with keystore.

### Generate App Bundle (Play Store)
```bash
./gradlew composeApp:bundleRelease
```
Create Android App Bundle for Play Store distribution.

## 🔧 KMP-Specific Commands

### Sync All Platforms
```bash
./gradlew --refresh-dependencies
```
Refresh and download all dependencies for all platforms.

### Generate IDE Files
```bash
./gradlew idea
```
Generate IntelliJ IDEA project files.

### Build Common Code Only
```bash
./gradlew compileKotlinMetadata
```
Compile only common Kotlin code to verify cross-platform compatibility.

### Platform-Specific Builds
```bash
# Android only
./gradlew compileDebugKotlinAndroid

# iOS only (if configured)
./gradlew compileDebugKotlinIosX64
```
Build specific platform code.

## 📋 Quick Reference (KMP)

| Command                                       | Purpose                        |
| --------------------------------------------- | ------------------------------ |
| `./gradlew composeApp:assembleDebug`          | Build debug APK                |
| `./gradlew composeApp:installDebug`           | Install to device              |
| `./gradlew composeApp:check`                   | Run all tests                  |
| `./gradlew composeApp:assembleRelease`        | Build release APK              |
| `./gradlew composeApp:bundleRelease`          | Build App Bundle               |
| `adb shell am start -n com.app/.MainActivity` | Launch app                     |
| `adb uninstall com.app`                        | Uninstall app                  |
| `adb devices`                                  | List connected devices         |
| `adb logcat -s App`                           | Monitor app logs               |
| `./gradlew clean`                              | Clean build artifacts          |
| `./gradlew build`                              | Build all platforms            |

## 📱 Device Information

- **Package Name**: `com.app`
- **Main Activity**: `com.app.MainActivity`
- **Debug APK Location**: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`
- **Release APK Location**: `composeApp/build/outputs/apk/release/composeApp-release.apk`
- **App Bundle Location**: `composeApp/build/outputs/bundle/release/composeApp-release.aab`
- **iOS Framework Location**: `composeApp/build/bin/iosX64/debugFramework/` (if configured)

_Last updated: May 10, 2026_
