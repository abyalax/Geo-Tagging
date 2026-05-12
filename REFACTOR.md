# KMP Architecture Refactoring Prompt - Complete Specification

## MISSION

Refactor Kotlin Multiplatform (KMP) Android app from tightly-coupled monolithic architecture to clean, layered architecture with clear separation of concerns. Result must be iOS-ready with zero Android SDK leaks in commonMain.

## CURRENT STATE ANALYSIS

### Architecture Problems

1. **NavigationManager** in commonMain imports `androidx.navigation.NavController` (Android SDK leak)
2. **AppNavHost** in commonMain is @Composable (Jetpack Compose only - iOS can't use)
3. **DashboardViewModel** in commonMain extends `androidx.lifecycle.ViewModel` (Android-specific)
4. **Routes** (LoginRoute, DashboardRoute) in commonMain are @Composable (Compose-specific)
5. **DashboardScreen** duplicated in both commonMain and androidMain (code duplication)
6. **GetSurveysUseCase** returns `emptyList()` (data missing - returns empty instead of 200+ surveys)
7. **Navigation event flow broken** (events emitted but nothing listens - login doesn't redirect)
8. **LoginViewModel** has syntax error + missing LoginPresenter class
9. **Dependency injection scattered** (DI setup in MainActivity, inconsistent patterns)

### File Locations (Current)

```
commonMain/
├── core/domain/model/
├── core/domain/repository/
├── core/domain/usecase/
├── core/navigation/NavigationManager.kt ❌
├── core/navigation/AppNavHost.kt ❌
├── core/theme/
└── features/
    ├── auth/presentation/route/ ❌
    ├── auth/presentation/viewmodel/ ❌
    ├── dashboard/presentation/route/ ❌
    ├── dashboard/presentation/screen/ ❌
    ├── dashboard/presentation/viewmodel/ ❌

androidMain/
├── MainActivity.kt
├── core/navigation/
├── features/dashboard/data/ ❌ (SurveyRepository with MOCK_SURVEYS)
└── features/dashboard/ui/screen/ ❌ (duplicate DashboardScreen)
```

## TARGET ARCHITECTURE

### Layered Structure

```
Domain Layer (commonMain/core/domain)
├── model/ (entities, value objects)
├── repository/ (interfaces only)
└── usecase/ (business logic)

Presentation Layer
├── commonMain/features/*/presentation/
│  ├── Presenter (pure Kotlin, state management)
│  ├── UiState (data class)
│  └── NavigationEventEmitter (interface)
└── androidMain/features/*/presentation/
   ├── ViewModel (Android-specific wrapper)
   ├── Route (Composable, @Composable)
   └── Screen (Composable, @Composable)

Data Layer
└── androidMain/features/*/data/
   ├── Repository implementation
   └── Models (platform-specific if needed)

Platform-Specific
├── androidMain/core/navigation/
│  ├── AndroidNavigationManager (NavController handler)
│  └── AppNavHost (Jetpack Compose NavHost)
└── androidMain/MainActivity (entry point, DI container)
```

## REFACTORING REQUIREMENTS

### Step 1: Fix Critical Bugs (Immediate)

#### 1.1 Fix GetSurveysUseCase

- **File**: `commonMain/features/dashboard/domain/usecase/GetSurveysUseCase.kt`
- **Change**: Return actual surveys from `SurveyRepository.getAllSurveys()`
- **Convert**: Platform-specific Survey model → domain Survey model
- **Output**: Dashboard loads 200+ surveys instead of empty list

#### 1.2 Create LoginPresenter (Missing)

- **File**: `commonMain/features/auth/presentation/LoginPresenter.kt` (NEW)
- **Contains**:
  - `data class LoginUiState(username: String, password: String, isLoading: Boolean, error: String?)`
  - `class LoginPresenter(loginUseCase: LoginUseCase, navigationEmitter: NavigationEventEmitter)`
  - Methods: `onUsernameChange()`, `onPasswordChange()`, `setLoading()`, `setError()`, `clearError()`
- **Note**: Platform-agnostic, no Android imports

#### 1.3 Fix LoginViewModel

- **File**: `androidMain/features/auth/presentation/viewmodel/LoginViewModel.kt`
- **Remove**: Duplicate `navigationManager` parameter
- **Add**: Navigation event state flow
- **Fix**: Proper error handling, SharedPreferences sync before navigation
- **Output**: Login compiles, login flow works

#### 1.4 Fix MainActivity

- **File**: `androidMain/MainActivity.kt`
- **Create**: Single `DefaultNavigationEventEmitter()` instance
- **Create**: `AndroidNavigationManager(navigationEventEmitter)` wrapper
- **Pass**: Both to `AppNavHost()`
- **Output**: Shared event emitter instance throughout app

#### 1.5 Fix AppNavHost

- **File**: `androidMain/navigation/AppNavHost.kt`
- **Add parameter**: `navigationEventEmitter: NavigationEventEmitter`
- **Add**: `LaunchedEffect(navigationEventEmitter.navigationEvents)` listener
- **Pass emitter**: To all route Composables
- **Output**: AppNavHost listens to events and calls `handleNavigation()`

#### 1.6 Fix LoginRoute

- **File**: `androidMain/features/auth/presentation/route/LoginRoute.kt`
- **Add parameter**: `navigationEventEmitter: NavigationEventEmitter`
- **Add**: `LaunchedEffect(viewModel.navigationEvents)` listener
- **Forward**: ViewModel events to global `navigationEventEmitter.navigate()`
- **Output**: Navigation events reach AppNavHost

#### 1.7 Fix SplashScreenRoute

- **File**: `androidMain/features/auth/presentation/route/SplashScreenRoute.kt`
- **Change**: Accept `navigationEventEmitter` instead of `NavHostController`
- **Use**: Proper `GetSessionUseCase` with `getCurrentSession().collectLatest { session → ... }`
- **Emit**: `navigationEventEmitter.navigate(NavigationEvent.NavigateToDashboard/Login)`
- **Output**: Splash uses event-based navigation

#### 1.8 Fix DashboardRoute

- **File**: `androidMain/features/dashboard/presentation/route/DashboardRoute.kt`
- **Add parameter**: `navigationEventEmitter: NavigationEventEmitter`
- **Add**: Event listener to forward ViewModel navigation events
- **Output**: Dashboard navigation events propagate

### Step 2: Establish Platform-Agnostic Navigation (commonMain)

#### 2.1 Create NavigationEventEmitter

- **File**: `commonMain/core/navigation/NavigationEventEmitter.kt` (replaces NavigationManager)
- **Interface**:
  ```kotlin
  interface NavigationEventEmitter {
    val navigationEvents: SharedFlow<NavigationEvent>
    fun navigate(event: NavigationEvent)
  }
  ```
- **Implementation**:
  ```kotlin
  class DefaultNavigationEventEmitter : NavigationEventEmitter {
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    override val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()
    override fun navigate(event: NavigationEvent) = _navigationEvents.tryEmit(event)
  }
  ```
- **Requirement**: ZERO Android imports

#### 2.2 Keep Routes.kt in commonMain

- **File**: `commonMain/core/navigation/Routes.kt` (unchanged, pure data)
- **Requirement**: Only sealed class with route strings and functions
- **No UI**: No @Composable, no Compose dependencies

#### 2.3 Keep NavigationEvent.kt in commonMain

- **File**: `commonMain/core/navigation/NavigationEvent.kt` (unchanged)
- **Requirement**: Sealed class with event types
- **No UI**: Pure data structure

### Step 3: Create Presenter Layer (commonMain)

#### 3.1 Dashboard Presenter

- **File**: `commonMain/features/dashboard/presentation/DashboardPresenter.kt` (extract from ViewModel)
- **Responsibility**: Pure business logic, state management
- **No Android**: No ViewModel, no lifecycle awareness
- **Methods**:
  - `suspend fun loadDashboardData()`
  - `fun onSurveyClick(survey: Survey)`
  - `fun onSearchChange(query: String)`
  - `fun onStatusFilterChange(status: SurveyStatus?)`
  - `fun onShareSurvey(survey: Survey)` (suspend)
  - `fun onOpenSurveyMap(survey: Survey)` (suspend)
  - `fun onLogout()` (suspend)
  - Navigation emission via `navigationEmitter.navigate(event)`
- **State**: `StateFlow<DashboardUiState>`

#### 3.2 Auth Presenter (Already created as LoginPresenter)

- **File**: `commonMain/features/auth/presentation/LoginPresenter.kt`
- **Methods**: `onUsernameChange()`, `onPasswordChange()`, `setLoading()`, `setError()`

#### 3.3 Profile Presenter (if needed)

- **File**: `commonMain/features/profile/presentation/ProfilePresenter.kt`
- **Responsibility**: Profile state, edit logic
- **Pattern**: Same as Dashboard/Login

### Step 4: Move UI to androidMain (Compose)

#### 4.1 Move AppNavHost

- **From**: `commonMain/core/navigation/AppNavHost.kt`
- **To**: `androidMain/core/navigation/AppNavHost.kt`
- **Requirement**: Accept `navigationEventEmitter` parameter
- **Listeners**: For each route, forward events to AppNavHost

#### 4.2 Move Route Composables

- **From**: `commonMain/features/*/presentation/route/`
- **To**: `androidMain/features/*/presentation/route/`
- **Files**:
  - `LoginRoute.kt`
  - `SplashScreenRoute.kt`
  - `DashboardRoute.kt`
  - `ProfileRoute.kt`
  - `VerificationRoute.kt`

#### 4.3 Move Screen Composables

- **From**: Duplicates in commonMain
- **To**: `androidMain/features/*/presentation/screen/` (single source)
- **Files**:
  - `LoginScreen.kt`
  - `DashboardScreen.kt`
  - `VerificationScreen.kt`
  - etc.

#### 4.4 Move ViewModels

- **From**: `commonMain/features/*/presentation/viewmodel/`
- **To**: `androidMain/features/*/presentation/viewmodel/`
- **Wrapper Pattern**:
  ```kotlin
  class DashboardViewModel(private val presenter: DashboardPresenter) : ViewModel() {
    val uiState = presenter.uiState
    init { viewModelScope.launch { presenter.loadDashboardData() } }
    fun onSurveyClick(survey: Survey) = presenter.onSurveyClick(survey)
    // ... delegate other methods
  }
  ```

### Step 5: Data Layer (androidMain)

#### 5.1 Keep AuthRepository Implementation

- **File**: `androidMain/core/data/repository/AuthRepositoryImpl.kt`
- **Uses**: SharedPreferences for mock auth
- **Implements**: `AuthRepository` interface from commonMain

#### 5.2 Keep SurveyRepository Implementation

- **File**: `androidMain/features/dashboard/data/SurveyRepository.kt`
- **Contains**: `MOCK_SURVEYS` (200+ items)
- **Methods**: `getAllSurveys()`, `getSurveyById()`, `updateSurveyStatus()`

### Step 6: Dependency Injection

#### 6.1 DI Container Pattern

- **File**: `androidMain/MainActivity.kt`
- **Create single instances**:
  ```kotlin
  val navigationEventEmitter = DefaultNavigationEventEmitter()
  val androidNavigationManager = AndroidNavigationManager(navigationEventEmitter)
  val authRepository = AuthRepositoryImpl(this)
  val getSessionUseCase = GetSessionUseCase(authRepository)
  val loginUseCase = LoginUseCase(authRepository)
  val logoutUseCase = LogoutUseCase(authRepository)
  val getSurveysUseCase = GetSurveysUseCase()
  val loginPresenter = LoginPresenter(loginUseCase, navigationEventEmitter)
  val dashboardPresenter = DashboardPresenter(getSurveysUseCase, getSessionUseCase, logoutUseCase, navigationEventEmitter)
  val loginViewModel = LoginViewModel(loginUseCase, androidNavigationManager, this)
  val dashboardViewModel = DashboardViewModel(getSurveysUseCase, getSessionUseCase, logoutUseCase, navigationEventEmitter)
  ```
- **Pass to AppNavHost**:
  ```kotlin
  AppNavHost(
    navigationEventEmitter = navigationEventEmitter,
    androidNavigationManager = androidNavigationManager,
    getSessionUseCase = getSessionUseCase,
    loginViewModel = loginViewModel,
    dashboardViewModel = dashboardViewModel
  )
  ```

## VALIDATION CHECKLIST

### Architecture Rules

- [ ] **No Android imports in commonMain** (`androidx.*` not allowed)
- [ ] **No Jetpack Compose in commonMain** (@Composable only in androidMain)
- [ ] **No ViewModel in commonMain** (extends androidx.lifecycle.ViewModel only in androidMain)
- [ ] **All platform differences abstracted** (NavigationEventEmitter interface, not NavController)
- [ ] **Single source of truth for each file** (no duplicates between commonMain/androidMain)

### Feature Completeness

- [ ] **Dashboard shows 200+ surveys** (GetSurveysUseCase returns real data)
- [ ] **Login flow works end-to-end** (NavigateToDashboard event fires and navigates)
- [ ] **Logout redirects to Login** (NavigateToLogin event fires)
- [ ] **Splash redirects based on login state** (uses GetSessionUseCase)
- [ ] **Filters/search work** (DashboardUiState properly filtered)
- [ ] **App restart behavior correct** (logged-in → Dashboard, not logged-in → Login)

### Code Quality

- [ ] **Presenters are testable** (pure Kotlin, no lifecycle dependencies)
- [ ] **No code duplication** (screens, models only in one place)
- [ ] **Clear layer boundaries** (domain → presenter → platform-UI → data)
- [ ] **Event-based navigation** (all navigation via NavigationEventEmitter)
- [ ] **DI pattern consistent** (single container in MainActivity)

### iOS Readiness

- [ ] **commonMain has zero iOS blockers** (can build iOS without androidMain)
- [ ] **Presenters shareable to iOS** (import and use directly)
- [ ] **Domain models shareable** (all use cases, models reusable)
- [ ] **Repository interfaces shareable** (iOS implements own)

## DELIVERABLES

### Source Files to Modify/Create

1. `commonMain/core/navigation/NavigationEventEmitter.kt` (CREATE/REPLACE)
2. `commonMain/features/auth/presentation/LoginPresenter.kt` (CREATE)
3. `commonMain/features/dashboard/domain/usecase/GetSurveysUseCase.kt` (MODIFY)
4. `commonMain/features/dashboard/presentation/DashboardPresenter.kt` (CREATE)
5. `androidMain/MainActivity.kt` (MODIFY - add DI)
6. `androidMain/core/navigation/AppNavHost.kt` (MODIFY - add listener)
7. `androidMain/core/navigation/AndroidNavigationManager.kt` (CREATE/MOVE)
8. `androidMain/features/auth/presentation/viewmodel/LoginViewModel.kt` (MODIFY)
9. `androidMain/features/auth/presentation/route/LoginRoute.kt` (MODIFY)
10. `androidMain/features/auth/presentation/route/SplashScreenRoute.kt` (MODIFY)
11. `androidMain/features/dashboard/presentation/viewmodel/DashboardViewModel.kt` (MODIFY)
12. `androidMain/features/dashboard/presentation/route/DashboardRoute.kt` (MODIFY)
13. `androidMain/features/dashboard/presentation/screen/DashboardScreen.kt` (CONSOLIDATE - single source)

### Files to Delete

- `commonMain/core/navigation/NavigationManager.kt` (replaced by NavigationEventEmitter)
- `commonMain/core/navigation/AppNavHost.kt` (moved to androidMain)
- `commonMain/features/auth/presentation/route/*.kt` (moved to androidMain)
- `commonMain/features/auth/presentation/viewmodel/LoginViewModel.kt` (moved to androidMain)
- `commonMain/features/dashboard/presentation/route/*.kt` (moved to androidMain)
- `commonMain/features/dashboard/presentation/screen/DashboardScreen.kt` (duplicated, keep only androidMain)
- `commonMain/features/dashboard/presentation/viewmodel/DashboardViewModel.kt` (moved to androidMain)
- `androidMain/features/dashboard/ui/screen/DashboardScreen.kt` (duplicate, consolidate with above)

### Documentation to Generate

- Architecture diagram (before/after SoC)
- File migration checklist
- API compatibility notes
- Testing guide for Presenters
- iOS implementation guide (template for reusing commonMain)

## TESTING STRATEGY

### Unit Tests (no Android framework needed)

```kotlin
// Test Presenters directly
@Test
fun testDashboardPresenterLoadSurveys() = runTest {
  val presenter = DashboardPresenter(mockUseCase, mockEmitter)
  presenter.loadDashboardData()
  assert(presenter.uiState.value.surveys.size == 200)
}

@Test
fun testLoginPresenterValidation() {
  val presenter = LoginPresenter(mockUseCase, mockEmitter)
  presenter.onUsernameChange("test")
  assert(presenter.uiState.value.username == "test")
}
```

### Integration Tests (with Android)

```kotlin
// Test ViewModels + Routes
@Test
fun testLoginNavigatesToDashboard() = runTest {
  val vm = LoginViewModel(mockUseCase, mockNavManager, context)
  vm.onLoginClick()
  // Assert navigation event fired
}
```

### Manual Testing

1. Cold start → Splash → Login/Dashboard (based on session)
2. Login with credentials → Dashboard with 200 surveys
3. Filter/search surveys
4. Click survey → Verification screen
5. Logout → back to Login
6. Restart while logged in → Dashboard (not Login)

## PERFORMANCE TARGETS

- App cold start: <2s (mostly Splash)
- Survey list render: <500ms (200 items, LazyColumn)
- Login process: <1s (mock auth)
- Navigation: instant (event-based, no delays)

## CONSTRAINTS

- Kotlin 1.9+
- Compose 1.6+
- Coroutines 1.7+
- Must maintain backward compatibility with existing API contracts
- No breaking changes to domain models (only reorganization)
- All platform differences hidden behind interfaces

## SUCCESS CRITERIA

1. **App runs without crashes** after all changes
2. **Login → Dashboard flow works** (navigation events propagate)
3. **Dashboard displays 200+ surveys** (GetSurveysUseCase returns data)
4. **No Android imports in commonMain** (verified via IDE/linter)
5. **Unit tests pass** for all Presenters (no Android framework needed)
6. **iOS developer can import commonMain** without build errors
7. **Code quality improved** (SOLID principles, clean architecture)
8. **No code duplication** between platforms
