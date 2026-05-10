# KMP Structure Examples (from docs)

## Complete Project Structure Example

```markdown
## 🏗️ Struktur Utama Proyek

```
Geo-Tagging/
├── 📁 composeApp/           # Module utama aplikasi KMP
├── 📁 iosApp/               # Proyek Xcode untuk iOS
├── 📁 gradle/               # Konfigurasi Gradle
├── 📁 docs/                 # Dokumentasi proyek
├── 📄 build.gradle.kts      # Build script root
├── 📄 gradlew               # Gradle wrapper (Linux/Mac)
├── 📄 gradlew.bat           # Gradle wrapper (Windows)
├── 📄 settings.gradle.kts   # Konfigurasi settings Gradle
├── 📄 gradle.properties     # Properties Gradle
├── 📄 local.properties      # Properties lokal (tidak di-track git)
└── 📄 README.md             # Dokumentasi utama
```
```

## Module Structure Example

```markdown
### 📱 Module `composeApp` (Aplikasi Utama KMP)

#### Struktur Lengkap `composeApp/`

```
└── 📁composeApp
    ├── 📁src/
    │   ├── 📁commonMain/kotlin/com/app/           # Shared business logic
    │   │   ├── 📄 App.kt                          # Application entry point
    │   │   ├── 📄 Platform.kt                     # Platform interface
    │   │   ├── 📁core/                            # Core components
    │   │   │   ├── 📁data/                        # Repository interfaces
    │   │   │   ├── 📁domain/                      # Domain layer
    │   │   │   │   ├── 📁usecase/        # Business logic use cases
    │   │   │   └── 📁navigation/                  # Navigation logic
    │   │   └── 📁features/                       # Feature modules
    │   └── 📁androidMain/kotlin/com/app/          # Android-specific
    │       ├── 📄 MainActivity.kt
    │       ├── 📄 Platform.android.kt
    │       ├── 📁core/                       # Core Android implementations
    │       └── 📁features/                   # Android-specific UI implementations
    └── 📄 build.gradle.kts
```
```

## Clean Architecture Example

```markdown
### 🏗️ Struktur Arsitektur `composeApp/` (Clean Architecture + KMP)

#### 📦 `commonMain/` - Shared Business Logic

Berisi semua business logic yang dapat digunakan di semua platform:

```
commonMain/kotlin/com/app/
├── 📁core/                    # Komponen inti yang shared
│   ├── 📁data/               # Repository interfaces
│   ├── 📁domain/             # Domain layer
│   │   ├── 📁model/          # Domain models
│   │   └── 📁usecase/        # Business logic use cases
│   ├── 📁navigation/         # Navigation logic
│   └── 📁theme/              # UI theme
├── 📁features/               # Feature modules
│   ├── 📁auth/               # Authentication feature
│   ├── 📁dashboard/          # Dashboard feature
│   ├── 📁profile/            # Profile feature
│   └── 📁verification/       # Verification feature
└── 📄 App.kt                 # Application entry point
```
```

## Feature Structure Example

```markdown
#### 🎯 `features/` - Fitur Aplikasi (Clean Architecture)

Setiap fitur mengikuti struktur Clean Architecture yang konsisten:

```
features/
├── 📁auth/                         # Fitur Autentikasi
│   └── 📁presentation/              # Presentation layer
│       ├── 📁route/                # Route handlers
│       │   ├── LoginRoute.kt
│       │   └── SplashScreenRoute.kt
│       └── 📁viewmodel/            # ViewModels
│           └── AuthViewModel.kt
├── 📁dashboard/                    # Fitur Dashboard
│   ├── 📁domain/                   # Domain layer
│   │   ├── 📁model/                # Domain models
│   │   │   ├── DashboardUiState.kt
│   │   │   ├── Survey.kt
│   │   │   ├── SurveyStats.kt
│   │   │   └── VerificationResult.kt
│   │   └── 📁usecase/             # Use cases
│   │       ├── GetSurveysUseCase.kt
│   │       ├── ShareSurveyUseCase.kt
│   │       └── OpenSurveyMapUseCase.kt
│   └── 📁presentation/            # Presentation layer
│       ├── 📁route/              # Route handlers
│       │   └── DashboardRoute.kt
│       ├── 📁viewmodel/          # ViewModels
│       │   └── DashboardViewModel.kt
│       └── 📁ui/                 # UI components
│           ├── 📁components/      # Reusable components
│           │   └── SurveyListItem.kt
│           └── 📁screen/          # Screens
│               └── DashboardScreen.kt
├── 📁profile/                      # Fitur Profile
│   └── 📁presentation/            # Presentation layer
│       ├── 📁route/              # Route handlers
│       │   └── ProfileRoute.kt
│       └── 📁viewmodel/          # ViewModels
│           └── ProfileViewModel.kt
└── 📁verification/                 # Fitur Verification
    └── 📁presentation/            # Presentation layer
        ├── 📁route/              # Route handlers
        │   └── VerificationRoute.kt
        └── 📁viewmodel/          # ViewModels
            └── VerificationViewModel.kt
```
```

## Platform Implementation Example

```markdown
#### 📱 `androidMain/` - Platform-Specific Implementations

Berisi implementasi khusus untuk platform Android:

```
androidMain/kotlin/com/app/
├── 📄 MainActivity.kt              # Activity utama Android
├── 📄 Platform.android.kt         # Platform implementation
├── 📁core/                       # Core Android implementations
│   ├── 📁common/                  # Constants Android
│   │   └── Constant.kt
│   ├── 📁navigation/              # Navigation Android
│   │   ├── AuthMiddleware.kt       # Authentication middleware
│   │   ├── ExplicitIntents.kt      # Explicit intent handling
│   │   └── ImplicitIntents.kt      # Implicit intent handling
│   └── 📁utils/                   # Android utilities
│       ├── MapUtils.kt             # Map utilities
│       └── VaildateCoordinate.kt   # Coordinate validation
└── 📁features/                   # Android-specific UI implementations
    ├── 📁dashboard/
    │   ├── 📁data/                 # Repository implementations
    │   │   └── SurveyRepository.kt  # Mock data repository
    │   └── 📁ui/                  # Android UI components
    │       ├── 📁components/
    │       │   └── SurveyListItem.kt
    │       └── 📁screen/
    │           ├── DashboardScreen.kt
    │           └── VerificationScreen.kt
    └── 📁auth/
        └── 📁ui/
            ├── 📁components/
            │   └── LoginForm.kt
            └── 📁screen/
                └── LoginScreen.kt
```
```

## Code Pattern Examples

### Expect/Actual Pattern
```kotlin
// commonMain
expect fun getPlatformName(): String

// androidMain
actual fun getPlatformName(): String = "Android"

// iosMain
actual fun getPlatformName(): String = "iOS"
```

### Use Case Pattern
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
```

### Repository Pattern
```kotlin
// Interface di commonMain
interface AuthRepository {
    suspend fun login(username: String, password: String): Result<UserSession>
    suspend fun logout(): Result<Unit>
    fun getSession(): Flow<UserSession?>
}

// Implementation di androidMain
class AuthRepositoryImpl @Inject constructor(
    private val context: Context
) : AuthRepository {
    // Implementation details
}
```

### Navigation Pattern
```kotlin
sealed class NavigationEvent {
    object NavigateToLogin : NavigationEvent()
    object NavigateToDashboard : NavigationEvent()
    data class NavigateToVerification(
        val surveyId: String,
        val locationName: String
    ) : NavigationEvent()
}
```

_Last updated: May 10, 2026_
