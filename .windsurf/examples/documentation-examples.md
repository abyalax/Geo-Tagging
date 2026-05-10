# Documentation Examples

## KMP Structure Documentation Example

```markdown
## 📱 Module `composeApp` (Aplikasi Utama KMP)

### Struktur Lengkap `composeApp/`
```

└── 📁composeApp
├── 📁src/
│ ├── 📁commonMain/kotlin/com/app/ # Shared business logic
│ │ ├── 📄 App.kt # Application entry point
│ │ ├── 📄 Platform.kt # Platform interface
│ │ ├── 📁core/ # Core components
│ │ │ ├── 📁data/ # Repository interfaces
│ │ │ ├── 📁domain/ # Domain layer
│ │ │ │ ├── 📁usecase/ # Business logic use cases
│ │ │ └── 📁navigation/ # Navigation logic
│ │ └── 📁features/ # Feature modules
│ └── 📁androidMain/kotlin/com/app/ # Android-specific
└── 📄 build.gradle.kts

```

```

## Clean Architecture Documentation Example

```markdown
### Clean Architecture Layers
```

Presentation Layer (UI + ViewModels)
↓
Domain Layer (Use Cases + Repository Interfaces)
↓
Data Layer (Repository Implementations)

````

### Use Case Pattern Example

```kotlin
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        password: String
    ): Result<UserSession> {
        return authRepository.login(username, password)
    }
}
````

````

## Command Documentation Example

```markdown
## 🚀 Deploy Commands (KMP)

### Install to Emulator (Android)

```bash
./gradlew composeApp:installDebug
````

Build and install debug APK to connected device/emulator.

### Build All Platforms

```bash
./gradlew build
```

Build for all configured platforms (Android, iOS if configured).

```

```

## Feature Documentation Example

```markdown
### ✅ Requirement 4: Navigation (Single Activity + Compose Navigation)

- **Authentication:** Use cases for session management & route protection
  - Files: AuthRepository, LoginUseCase, GetSessionUseCase, LogoutUseCase
- **Navigation:** Single-activity with Compose Navigation
  - Files: AppNavHost.kt, NavigationManager.kt, Routes.kt, NavigationEvent.kt
- **Navigation Events:** Centralized event-based navigation
  - File: NavigationManager.kt, NavigationEvent.kt
- **Architecture:** Clean Architecture with separated navigation logic
- **Status:** Complete
```

```

_Last updated: April 25, 2026_
```
