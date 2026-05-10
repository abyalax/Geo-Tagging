.DEFAULT_GOAL := help

.PHONY: \
	help \
	build \
	release \
	bundle \
	signed-release \
	clean \
	rebuild \
	compile-check \
	stacktrace \
	sync \
	clean-sync \
	no-cache \
	install \
	install-release \
	reinstall \
	uninstall \
	run \
	restart \
	stop \
	clear-data \
	logs \
	logcat \
	devices \
	devices-full \
	emulators \
	emulator \
	emulator-start \
	emulator-clean \
	emulator-kill \
	emulator-kill-all \
	adb-reset \
	space \
	export-debug \
	export-release \
	dev \
	debug-workflow \
	release-workflow \
	fresh-install \
	fresh-reinstall

# ============================================
# Config
# ============================================

APP_ID=com.app
MAIN_ACTIVITY=.MainActivity
MODULE=composeApp
AVD=Medium_Phone_API_36.1

GRADLE=gradlew.bat

DEBUG_APK=$(MODULE)/build/outputs/apk/debug/$(MODULE)-debug.apk
RELEASE_APK=$(MODULE)/build/outputs/apk/release/$(MODULE)-release.apk

# ============================================
# Build Commands
# ============================================

clean:
	$(GRADLE) clean

build:
	$(GRADLE) $(MODULE):assembleDebug

release:
	$(GRADLE) $(MODULE):assembleRelease

bundle:
	$(GRADLE) $(MODULE):bundleRelease

compile-check:
	$(GRADLE) compileDebugKotlinAndroid

stacktrace:
	$(GRADLE) build --stacktrace

rebuild:
	$(GRADLE) clean build --no-configuration-cache --refresh-dependencies

sync:
	$(GRADLE) --refresh-dependencies

clean-sync:
	$(GRADLE) clean build --refresh-dependencies

no-cache:
	$(GRADLE) clean build --no-configuration-cache

# ============================================
# Install / Deploy
# ============================================

install:
	$(GRADLE) $(MODULE):installDebug

install-release:
	$(GRADLE) $(MODULE):installRelease

uninstall:
	adb uninstall $(APP_ID)

reinstall:
	adb uninstall $(APP_ID) || true
	$(GRADLE) $(MODULE):installDebug

# ============================================
# Fresh Install / Reinstall
# ============================================

fresh-install:
	@echo "=== Fresh Install Workflow ==="
	@echo "1. Cleaning project..."
	$(GRADLE) clean
	@echo "2. Building debug APK..."
	$(GRADLE) $(MODULE):assembleDebug
	@echo "3. Uninstalling existing app (if any)..."
	adb uninstall $(APP_ID) || true
	@echo "4. Installing fresh APK..."
	$(GRADLE) $(MODULE):installDebug
	@echo "5. Clearing app data..."
	adb shell pm clear $(APP_ID) || true
	@echo "6. Starting app..."
	adb shell am start -n $(APP_ID)/$(MAIN_ACTIVITY)
	@echo "=== Fresh Install Complete ==="

fresh-reinstall:
	@echo "=== Fresh Reinstall Workflow ==="
	@echo "1. Force stopping app..."
	adb shell am force-stop $(APP_ID) || true
	@echo "2. Clearing app data..."
	adb shell pm clear $(APP_ID) || true
	@echo "3. Uninstalling app..."
	adb uninstall $(APP_ID)
	@echo "4. Cleaning project..."
	$(GRADLE) clean
	@echo "5. Building debug APK..."
	$(GRADLE) $(MODULE):assembleDebug
	@echo "6. Installing fresh APK..."
	$(GRADLE) $(MODULE):installDebug
	@echo "7. Starting app..."
	adb shell am start -n $(APP_ID)/$(MAIN_ACTIVITY)
	@echo "=== Fresh Reinstall Complete ==="

# ============================================
# App Runtime
# ============================================

run:
	adb shell am start -n $(APP_ID)/$(MAIN_ACTIVITY)

restart:
	adb shell am force-stop $(APP_ID)
	adb shell am start -n $(APP_ID)/$(MAIN_ACTIVITY)

stop:
	adb shell am force-stop $(APP_ID)

clear-data:
	adb shell pm clear $(APP_ID)

# ============================================
# Emulator Commands
# ============================================

emulator:
	@if "$(NAME)"=="" ( \
		echo Usage: make emulator NAME=<emulator_name> \
		echo Available emulators: \
		emulator -list-avds \
	) else ( \
		echo Starting emulator: $(NAME) \
		start "Android Emulator" emulator -avd $(NAME) \
	)

emulators:
	emulator -list-avds

emulator-start:
	emulator -avd $(AVD)

emulator-clean:
	emulator -avd $(AVD) -no-snapshot-load

devices:
	adb devices

devices-full:
	adb devices -l

emulator-kill:
	adb -s emulator-5554 emu kill

emulator-kill-all:
	@for /f "tokens=1" %%i in ('adb devices ^| findstr emulator') do adb -s %%i emu kill

# ============================================
# Logs
# ============================================

logs:
	adb logcat -s App

logcat:
	adb logcat

# ============================================
# ADB Utilities
# ============================================

adb-reset:
	adb kill-server
	adb start-server

space:
	adb shell df /data/local/tmp

# ============================================
# Export APK
# ============================================

export-debug:
	copy $(DEBUG_APK) .\ScrollGuard-debug.apk

export-release:
	copy $(RELEASE_APK) .\ScrollGuard-release.apk

# ============================================
# Development Workflow
# ============================================

dev:
	$(GRADLE) clean
	$(GRADLE) $(MODULE):assembleDebug
	$(GRADLE) $(MODULE):installDebug
	adb shell am start -n $(APP_ID)/$(MAIN_ACTIVITY)

debug-workflow:
	$(GRADLE) compileDebugKotlinAndroid
	$(GRADLE) compileDebugKotlinAndroid --stacktrace
	$(GRADLE) $(MODULE):assembleDebug
	$(GRADLE) $(MODULE):installDebug

release-workflow:
	$(GRADLE) clean
	$(GRADLE) $(MODULE):assembleRelease
	$(GRADLE) $(MODULE):installRelease
	copy $(RELEASE_APK) .\ScrollGuard-release.apk

# ============================================
# Help
# ============================================

help:
	@echo ""
	@echo "============================================"
	@echo " Android Compose App Makefile"
	@echo "============================================"
	@echo ""
	@echo "[ Build ]"
	@echo "  make clean               Clean project"
	@echo "  make build               Build debug APK"
	@echo "  make release             Build release APK"
	@echo "  make bundle              Build app bundle"
	@echo "  make compile-check       Kotlin compile check"
	@echo "  make stacktrace          Full build with stacktrace"
	@echo "  make rebuild             Clean rebuild"
	@echo "  make sync                Refresh dependencies"
	@echo "  make clean-sync          Clean + refresh deps"
	@echo "  make no-cache            Disable configuration cache"
	@echo ""
	@echo "[ Install ]"
	@echo "  make install             Install debug APK"
	@echo "  make install-release     Install release APK"
	@echo "  make uninstall           Uninstall app"
	@echo "  make reinstall           Fresh reinstall"
	@echo "  make fresh-install       Full fresh install workflow"
	@echo "  make fresh-reinstall     Full fresh reinstall workflow"
	@echo ""
	@echo "[ Runtime ]"
	@echo "  make run                 Launch app"
	@echo "  make restart             Restart app"
	@echo "  make stop                Force stop app"
	@echo "  make clear-data          Clear app data"
	@echo ""
	@echo "[ Emulator ]"
	@echo "  make emulators           List AVD emulators"
	@echo "  make emulator NAME=<name> Start specific emulator"
	@echo "  make emulator-clean      Clean start emulator"
	@echo "  make emulator-kill       Kill emulator-5554"
	@echo "  make emulator-kill-all   Kill all emulators"
	@echo "  make devices             List connected devices"
	@echo "  make devices-full        Detailed devices"
	@echo ""
	@echo "[ Logs ]"
	@echo "  make logs                App logs"
	@echo "  make logcat              Full logcat"
	@echo ""
	@echo "[ Export ]"
	@echo "  make export-debug        Export debug APK"
	@echo "  make export-release      Export release APK"
	@echo ""
	@echo "[ Workflow ]"
	@echo "  make dev                 Full dev cycle"
	@echo "  make debug-workflow      Debug workflow"
	@echo "  make release-workflow    Production workflow"
	@echo ""
