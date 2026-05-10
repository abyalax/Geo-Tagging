---
trigger: always_on
description: Architecture and coding rules/standards
---

# Architecture & Coding Standards

## 🏗️ Architecture Rules (NON-NEGOTIABLE)

### Core Principles

**Dependency Rule**: Dependencies point inward → Presentation → Domain ← Data  
**Single Responsibility**: Setiap class punya satu reason to change  
**Separation of Concerns**: Clear boundaries antar layers  
**Domain Independence**: Domain layer TIDAK ada framework dependencies

### Layer Structure

```
PRESENTATION LAYER (androidMain/features/*/presentation/)
    ├── Jetpack Compose UI (Screens, Components)
    ├── ViewModels (State management)
    ├── Navigation (Routes, Navigation Manager)
    └── Depends on Domain only

DOMAIN LAYER (commonMain/core/domain/)
    ├── Use Cases (Business logic)
    ├── Repository Interfaces (Contracts)
    ├── Domain Models (Business entities)
    └── NO Framework dependencies

DATA LAYER (androidMain/core/data/)
    ├── Repository Implementations
    ├── Data Sources (Local, Remote)
    ├── Mappers (Domain ↔ External models)
    └── Framework-specific code allowed
```

## 📱 KMP Architecture Rules

### Shared vs Platform-Specific

**commonMain/** (Shared)

- ✅ Business logic (use cases)
- ✅ Domain models
- ✅ Repository interfaces
- ✅ Navigation logic
- ❌ No Android/iOS specific code

**androidMain/** (Platform-Specific)

- ✅ UI Screens (Compose)
- ✅ ViewModels
- ✅ Repository implementations
- ✅ Android-specific components
- ✅ SharedPreferences, Context, etc.

**iosMain/** (Platform-Specific)

- ✅ iOS UI (SwiftUI / Compose Multiplatform)
- ✅ iOS-specific implementations
- ✅ Native iOS APIs

### Expected Common Structure

```
commonMain/kotlin/com/app/
├── core/
│   ├── domain/
│   │   ├── model/          # Domain entities (Survey, User, etc.)
│   │   ├── repository/     # Repository interfaces
│   │   └── usecase/        # Use cases (GetSurveysUseCase, etc.)
│   └── navigation/         # Navigation logic
└── features/
    ├── auth/
    │   └── domain/         # Auth use cases
    ├── dashboard/
    │   ├── domain/         # Survey domain models
    │   └── presentation/   # Dashboard screens
    ├── verification/
    └── profile/

androidMain/kotlin/com/app/
├── MainActivity.kt
├── core/
│   └── data/              # Repository implementations
└── features/
    ├── dashboard/
    │   ├── data/          # Mock/Real data source
    │   └── ui/            # Compose screens
    └── auth/
        └── ui/            # Login screen
```

## 🎯 Navigation Architecture Rules

### Single Activity Principle

- ✅ **ONLY ONE Activity**: MainActivity untuk semua navigation
- ✅ **Compose Navigation**: Semua screen transitions via Compose Navigation
- ✅ **Type-Safe Routes**: Routes sebagai sealed class/object
- ✅ **Centralized Management**: NavigationManager untuk semua navigation logic
- ❌ **NO Direct NavController calls**: Avoid direct navigation di UI

### Route Definition

```kotlin
sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object Dashboard : Routes("dashboard")
    data object Profile : Routes("profile")
    data object Verification : Routes("verification/{surveyId}/{locationName}") {
        fun createRoute(surveyId: String, locationName: String) =
            "verification/${Uri.encode(surveyId)}/${Uri.encode(locationName)}"
    }
}
```

### Navigation Rules

- ✅ Use `Routes.X.route` ALWAYS (never hardcoded strings)
- ✅ URL encode route parameters dengan special characters: `Uri.encode(parameter)`
- ✅ Use sealed class untuk type-safe routing
- ✅ LaunchedEffect untuk navigation logic di composables
- ❌ NO direct NavController calls dalam UI logic

## 🏛️ Clean Architecture Layer Rules

### Domain Layer Rules (commonMain/core/domain/)

**Allowed**:

- Pure Kotlin functions
- Data classes
- Repository interfaces
- Use case implementations
- Domain models

**Forbidden**:

- Android imports (Context, View, etc.)
- iOS imports (UIKit, Foundation, etc.)
- Framework-specific code
- External library dependencies

### Use Case Pattern

```kotlin
// ✅ CORRECT
class GetSurveysUseCase @Inject constructor(
    private val surveyRepository: SurveyRepository
) {
    suspend operator fun invoke(
        searchQuery: String = "",
        statusFilter: SurveyStatus? = null
    ): Result<List<Survey>> {
        return try {
            val surveys = surveyRepository.getSurveys()
            val filtered = surveys.filter { survey ->
                // Filtering logic
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Data Layer Rules (androidMain/core/data/)

**Responsibilities**:

- Implement repository interfaces
- Handle data source (local/remote)
- Map external data → domain models
- Error handling dan conversion
- Platform-specific APIs allowed

**Repository Implementation**:

```kotlin
class SurveyRepositoryImpl @Inject constructor(
    private val localDataSource: SurveyLocalDataSource,
    private val remoteDataSource: SurveyRemoteDataSource,
    private val mapper: SurveyMapper
) : SurveyRepository {
    override suspend fun getSurveys(): List<Survey> {
        return try {
            val remoteSurveys = remoteDataSource.getSurveys()
            localDataSource.cacheSurveys(remoteSurveys)
            remoteSurveys.map { mapper.toDomain(it) }
        } catch (e: Exception) {
            localDataSource.getSurveys().map { mapper.toDomain(it) }
        }
    }
}
```

### Presentation Layer Rules (androidMain/features/\*/presentation/)

**Responsibilities**:

- UI state management dengan ViewModel
- Composable screens dan components
- User event handling
- Navigation triggering

**ViewModel Pattern**:

```kotlin
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getSurveysUseCase: GetSurveysUseCase,
    private val updateSurveyUseCase: UpdateSurveyUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadSurveys()
    }

    private fun loadSurveys() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getSurveysUseCase().fold(
                onSuccess = { surveys ->
                    _uiState.update {
                        it.copy(surveys = surveys, isLoading = false)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message, isLoading = false)
                    }
                }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadSurveys()
    }
}
```

## 💾 State Management Rules

### Immutability

- ✅ **All state immutable**: Use `data class` untuk UI state
- ✅ **StateFlow only**: For reactive state management
- ❌ **NO mutable state**: Avoid `var` dalam state classes

### State Hoisting

```kotlin
// ✅ CORRECT: State dalam ViewModel
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardContent(
        surveys = uiState.surveys,
        isLoading = uiState.isLoading,
        onSearchChange = viewModel::onSearchQueryChanged
    )
}

// ❌ WRONG: Local state untuk large datasets
@Composable
fun DashboardScreen() {
    var surveys by remember { mutableStateOf(emptyList<Survey>()) }
    // Bad: No proper lifecycle management
}
```

### StateFlow Usage

```kotlin
// ✅ CORRECT: StateFlow in ViewModel
class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun updateState(block: DashboardUiState.() -> DashboardUiState) {
        _uiState.update(block)
    }
}

// ✅ In Composable
val uiState by viewModel.uiState.collectAsState()
```

## 🎨 Jetpack Compose Rules

### Composable Function Rules

- ✅ **PascalCase** naming untuk composables
- ✅ **Preview** untuk semua screens
- ✅ **Parameter order**: Required first, optional last
- ✅ **Modifier parameter** di akhir
- ❌ **NO business logic** dalam composables

### Size Limits

- Screen composables: **< 200 lines**
- Component composables: **< 100 lines**
- Helper functions: **< 50 lines**
- Extract ketika melebihi limits

### LazyColumn Rules (untuk 200+ items)

```kotlin
// ✅ CORRECT
LazyColumn(
    modifier = Modifier.fillMaxSize()
) {
    items(
        items = surveys,
        key = { survey -> survey.id }  // ✅ Stable key
    ) { survey ->
        SurveyListItem(survey = survey)
    }
}

// ❌ WRONG: No keys
LazyColumn {
    items(surveys.size) { index ->
        SurveyListItem(survey = surveys[index])
    }
}
```

### Modifier Chaining

```kotlin
// ✅ CORRECT: Logical order
Box(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(Color.White)
        .clickable { /* ... */ },
    contentAlignment = Alignment.Center
) { }
```

## 🔒 Dependency Rules

### Allowed Dependencies

- **Presentation → Domain**: Through use cases ONLY
- **Data → Domain**: Implement interfaces
- **Data → External APIs**: Framework, DB, networking
- ❌ **Presentation → Repository**: NEVER directly
- ❌ **Domain → Anything**: NEVER except pure Kotlin

### Dependency Injection

```kotlin
// ✅ CORRECT: Inject dependencies
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getSurveysUseCase: GetSurveysUseCase
) : ViewModel() { }

// ❌ WRONG: Service locator pattern
val viewModel = DashboardViewModel(
    SurveyRepository()  // Don't instantiate
)
```

## 🎯 Coding Style Standards

### Naming Conventions

- **Use Cases**: Verb + Noun (GetSurveysUseCase, UpdateSurveyUseCase)
- **Repositories**: Entity + Repository (SurveyRepository, AuthRepository)
- **ViewModels**: Screen + ViewModel (DashboardViewModel, LoginViewModel)
- **UI State**: Entity + UiState (DashboardUiState, LoginUiState)
- **Private fields**: Underscore prefix (\_uiState)

### Kotlin Language Rules

- ✅ **Null safety**: Use `?` explicitly
- ✅ **Immutable by default**: Prefer `val` over `var`
- ✅ **Data classes**: For data holders
- ✅ **Sealed classes**: For restricted types
- ✅ **Extension functions**: For utilities
- ❌ **Abbreviations**: Use full names (user not usr)

### Documentation

- ✅ **KDoc** untuk semua public APIs
- ✅ **Why not What**: Comment pada non-obvious logic
- ✅ **Business rules**: Document domain knowledge
- ❌ **Obvious comments**: Don't comment `val x = 5` style

```kotlin
// ✅ CORRECT
/**
 * Filters surveys based on search query dan status.
 *
 * @param query Search query untuk filter title/description
 * @param status Optional status filter (null = semua status)
 * @return Filtered list of surveys
 */
fun filterSurveys(query: String, status: SurveyStatus?): List<Survey>

// ❌ WRONG
// Filter surveys by status
fun filterSurveys(q: String, st: SurveyStatus?): List<Survey>
```

### Code Formatting

- **Indentation**: 2 spaces (NO tabs)
- **Line length**: Max 120 characters
- **Trailing commas**: Use dalam multi-line
- **Ktlint**: Follow formatting rules

## ⚡ Performance Rules

### Memory Management

- ✅ **LazyColumn** untuk lists > 100 items
- ✅ **Stable keys** dalam LazyColumn
- ✅ **Minimal recomposition** scope
- ❌ **NO object creation** dalam hot paths

### UI Performance Target

- **60fps** smooth scrolling
- **200+ items** without lag
- **Real-time search** instant filtering
- **< 16ms** frame time

## 🧪 Testing Rules

### Unit Tests

- ✅ Test all use cases
- ✅ Mock external dependencies
- ✅ Test state changes dalam ViewModel
- ✅ Test repository implementations

### UI Tests

- ✅ Test user flows
- ✅ Test navigation
- ✅ Test state updates
- ✅ Use @Preview untuk visual testing

---

_Last updated: May 10, 2026_
