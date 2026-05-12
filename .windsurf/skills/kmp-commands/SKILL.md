---
name: KMP Command Skills
description: How To Use Command That Available on this project
---

# KMP Commands Knowledge (from Makefile)

## Overview

Documentation of essential **Makefile commands** for **Kotlin Multiplatform** development, debugging, deployment, and app distribution for **Geo-Tagging** application with **Clean Architecture**.

## Prerequisites

- Android SDK installed
- Android Emulator configured (Medium_Phone_API_36.1)
- ADB available in PATH
- Make available in PATH
- Gradle wrapper configured

## � Development Commands (Makefile)

### Build Debug APK

```bash
make build
```

Build debug APK for testing using Makefile.

### Install to Device/Emulator

```bash
make install
```

Build and install debug APK to connected device/emulator using Makefile.

### Run Application

```bash
make run
```

Launch the installed application on connected device/emulator using Makefile.

### Monitor Application Logs

```bash
make logs
```

Monitor application logs with filtered tags for LoginScreen, LoginViewModel, App, MainActivity, and other key components using Makefile.

### Full Development Cycle

```bash
make dev
```

Complete development workflow: clean project, build debug APK, install to device, and launch app using Makefile.

## 🔧 Additional Makefile Commands

### Clean Project

```bash
make clean
```

Remove all build artifacts and start fresh.

### Rebuild Project

```bash
make rebuild
```

Clean build with refreshed dependencies.

### Fresh Install Workflow

```bash
make fresh-install
```

Complete fresh install: clean, build, uninstall existing app, install new APK, clear data, and launch app.

### Fresh Reinstall Workflow

```bash
make fresh-reinstall
```

Complete fresh reinstall: force stop, clear data, uninstall, clean, build, install, and launch app.

### Restart Application

```bash
make restart
```

Force stop and restart the application.

### Stop Application

```bash
make stop
```

Force stop the application.

### Clear Application Data

```bash
make clear-data
```

Clear all application data while keeping the app installed.

### List Connected Devices

```bash
make devices
```

List all connected Android devices and emulators.

### Full Logcat

```bash
make logcat
```

Monitor full Android system logs.

### Export Debug APK

```bash
make export-debug
```

Copy debug APK to project root as ScrollGuard-debug.apk.

### Export Release APK

```bash
make export-release
```

Copy release APK to project root as ScrollGuard-release.apk.

## 📦 Build & Release Commands (Makefile)

### Build Release APK

```bash
make release
```

Build production-ready APK for Android distribution.

### Build App Bundle

```bash
make bundle
```

Create Android App Bundle for Play Store distribution.

### Install Release APK

```bash
make install-release
```

Build and install release APK to connected device/emulator.

### Uninstall Application

```bash
make uninstall
```

Uninstall the application from connected device/emulator.

### Reinstall Application

```bash
make reinstall
```

Uninstall existing app and install fresh debug APK.

## 📋 Quick Reference (Makefile)

| Command                | Purpose                     |
| ---------------------- | --------------------------- |
| `make build`           | Build debug APK             |
| `make install`         | Install to device/emulator  |
| `make run`             | Launch app                  |
| `make logs`            | Monitor app logs            |
| `make dev`             | Full dev cycle              |
| `make fresh-install`   | Fresh install workflow      |
| `make fresh-reinstall` | Fresh reinstall workflow    |
| `make restart`         | Restart app                 |
| `make stop`            | Force stop app              |
| `make clear-data`      | Clear app data              |
| `make uninstall`       | Uninstall app               |
| `make reinstall`       | Reinstall app               |
| `make clean`           | Clean project               |
| `make rebuild`         | Clean rebuild               |
| `make release`         | Build release APK           |
| `make bundle`          | Build app bundle            |
| `make install-release` | Install release APK         |
| `make export-debug`    | Export debug APK            |
| `make export-release`  | Export release APK          |
| `make devices`         | List connected devices      |
| `make logcat`          | Full system logs            |
| `make help`            | Show all available commands |

## 📱 Device Information

- **Package Name**: `com.app`
- **Main Activity**: `com.app.MainActivity`
- **Debug APK Location**: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`
- **Release APK Location**: `composeApp/build/outputs/apk/release/composeApp-release.apk`
- **App Bundle Location**: `composeApp/build/outputs/bundle/release/composeApp-release.aab`
- **iOS Framework Location**: `composeApp/build/bin/iosX64/debugFramework/` (if configured)

_Last updated: May 11, 2026_
