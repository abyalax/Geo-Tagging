---
description: Development workflows and common task patterns
auto_execution_mode: 2
---

# Development Workflows & Tasks

## 🚀 Common Development Workflows

### Workflow 1: Adding New Feature

**Step-by-step guide untuk menambah fitur baru**

#### 1.1 Define Domain Logic (commonMain/core/domain/)

```kotlin
// 1. Create domain model
data class NewEntity(
    val id: String,
    val name: String,
    // ... business fields
)

// 2. Create repository interface
interface NewRepository {
    suspend fun getItems(): List<NewEntity>
    suspend fun updateItem(item: NewEntity): Result<NewEntity>
}

// 3. Create use case(s)
class GetNewItemsUseCase @Inject constructor(
    private val repository: NewRepository
) {
    suspend operator fun invoke(): Result<List<NewEntity>> {
        return try {
            Result.success(repository.getItems())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**Checklist**:

- [ ] Domain models defined
- [ ] Repository interface created
- [ ] Use cases implemented
- [ ] No framework dependencies
- [ ] Follow naming conventions

#### 1.2 Create Repository Implementation (androidMain/core/data/)

```kotlin
// 1. Create data models
data class NewEntityEntity(
    val id: String,
    val name: String
)

// 2. Create data source
class NewLocalDataSource @Inject constructor(
    private val context: Context
) {
    suspend fun getItems(): List<NewEntityEntity> {
        // Load from local DB/SharedPrefs
        return emptyList()
    }
}

// 3. Implement repository
class NewRepositoryImpl @Inject constructor(
    private val localDataSource: NewLocalDataSource,
    private val mapper: NewEntityMapper
) : NewRepository {
    override suspend fun getItems(): List<NewEntity> {
        return localDataSource.getItems()
            .map { mapper.toDomain(it) }
    }
}

// 4. Create mapper
class NewEntityMapper {
    fun toDomain(entity: NewEntityEntity): NewEntity {
        return NewEntity(
            id = entity.id,
            name = entity.name
        )
    }
}
```

**Checklist**:

- [ ] Entity models created
- [ ] Data source implemented
- [ ] Repository implementation done
- [ ] Mapper created
- [ ] Error handling included

#### 1.3 Create ViewModel (androidMain/features/newfeature/presentation/)

```kotlin
data class NewFeatureUiState(
    val items: List<NewEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedItem: NewEntity? = null
)

@HiltViewModel
class NewFeatureViewModel @Inject constructor(
    private val getItemsUseCase: GetNewItemsUseCase,
    private val updateItemUseCase: UpdateNewItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewFeatureUiState())
    val uiState: StateFlow<NewFeatureUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getItemsUseCase().fold(
                onSuccess = { items ->
                    _uiState.update {
                        it.copy(items = items, isLoading = false)
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

    fun onItemSelected(item: NewEntity) {
        _uiState.update { it.copy(selectedItem = item) }
    }
}
```

**Checklist**:

- [ ] UI state class created
- [ ] ViewModel with state flow
- [ ] Use cases injected
- [ ] Error handling implemented
- [ ] Business logic separated

#### 1.4 Create UI Screens (androidMain/features/newfeature/presentation/ui/)

```kotlin
@Composable
fun NewFeatureScreen(
    onNavigateToDetail: (NewEntity) -> Unit
) {
    val viewModel: NewFeatureViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            ErrorScreen(error = uiState.error)
        }
        else -> {
            NewFeatureContent(
                items = uiState.items,
                onItemClick = { item ->
                    viewModel.onItemSelected(item)
                    onNavigateToDetail(item)
                }
            )
        }
    }
}

@Composable
private fun NewFeatureContent(
    items: List<NewEntity>,
    onItemClick: (NewEntity) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            NewEntityCard(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
@Preview
fun NewFeatureScreenPreview() {
    NewFeatureScreen(onNavigateToDetail = {})
}
```

**Checklist**:

- [ ] Screen composable created
- [ ] StateFlow collected properly
- [ ] State handling (loading/error/content)
- [ ] Preview included
- [ ] Modifier chaining correct
- [ ] < 200 lines per screen

#### 1.5 Add Navigation (androidMain/core/navigation/)

```kotlin
// Update Routes.kt
sealed class Routes(val route: String) {
    // ... existing routes
    data object NewFeature : Routes("newfeature")
    data object NewFeatureDetail : Routes("newfeature/{itemId}") {
        fun createRoute(itemId: String) = "newfeature/${Uri.encode(itemId)}"
    }
}

// Update AppNavHost.kt
NavHost(navController = navController, startDestination = startDestination) {
    // ... existing routes

    composable(Routes.NewFeature.route) {
        NewFeatureScreen(
            onNavigateToDetail = { item ->
                navController.navigate(
                    Routes.NewFeatureDetail.createRoute(item.id)
                )
            }
        )
    }

    composable(
        route = Routes.NewFeatureDetail.route,
        arguments = listOf(
            navArgument("itemId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
        NewFeatureDetailScreen(itemId = itemId)
    }
}
```

**Checklist**:

- [ ] Routes added to sealed class
- [ ] Route parameters URL-encoded
- [ ] Navigation graph updated
- [ ] Back stack management correct
- [ ] Arguments properly defined

#### 1.6 Testing

```kotlin
// Test Use Case
class GetNewItemsUseCaseTest {
    private lateinit var repository: FakeNewRepository
    private lateinit var useCase: GetNewItemsUseCase

    @Before
    fun setup() {
        repository = FakeNewRepository()
        useCase = GetNewItemsUseCase(repository)
    }

    @Test
    fun `should return items successfully`() = runTest {
        val result = useCase()
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
    }
}

// Test ViewModel
class NewFeatureViewModelTest {
    private lateinit var viewModel: NewFeatureViewModel

    @Before
    fun setup() {
        val useCase = mockk<GetNewItemsUseCase>()
        coEvery { useCase() } returns Result.success(listOf())
        viewModel = NewFeatureViewModel(useCase, mockk())
    }

    @Test
    fun `should load items on init`() = runTest {
        assertEquals(0, viewModel.uiState.value.items.size)
    }
}
```

**Checklist**:

- [ ] Use case tests written
- [ ] ViewModel tests written
- [ ] Repository tests written
- [ ] Error cases covered
- [ ] UI tests written (if needed)

---

### Workflow 2: Fixing Navigation Issues

**Step-by-step untuk fix navigation problems**

#### 2.1 Identify Issue

```
❌ Issue: User logs in → immediately logs out → infinite loop
```

#### 2.2 Check TopAppBar Logic

```kotlin
// ❌ WRONG: Code in composable body
@Composable
fun TopAppBar(...) {
    onLogout.invoke()  // ❌ Executes every recomposition!
    navController?.navigate("login")  // ❌ Wrong!

    // UI code...
}

// ✅ CORRECT: Only in callbacks
@Composable
fun TopAppBar(...) {
    // NO logout logic here

    Surface { /* UI only */ }

    DropdownMenu {
        DropdownMenuItem(
            text = { Text("Logout") },
            onClick = {
                onLogout()  // ✅ Only when clicked
                navController?.navigate(Routes.Login.route)
            }
        )
    }
}
```

#### 2.3 Check Route Strings

```kotlin
// ❌ WRONG: Hardcoded strings
navController?.navigate("login")
navController.navigate("login") { popUpTo("login") { inclusive = true } }

// ✅ CORRECT: Use Routes sealed class
navController?.navigate(Routes.Login.route)
navController.navigate(Routes.Login.route) {
    popUpTo(Routes.Login.route) { inclusive = true }
}
```

#### 2.4 Check Parameter Encoding

```kotlin
// ❌ WRONG: Special characters break navigation
"verification/$surveyId/$locationName"
// Result: "verification/123/Location/With/Slashes" → broken!

// ✅ CORRECT: URL encode parameters
"verification/${Uri.encode(surveyId)}/${Uri.encode(locationName)}"
// Result: "verification/123/Location%2FWith%2FSlashes" → works!
```

**Checklist**:

- [ ] No logout logic in composable body
- [ ] All navigation uses Routes sealed class
- [ ] Route parameters are URL-encoded
- [ ] Back stack management correct
- [ ] LaunchedEffect for navigation logic

---

### Workflow 3: Debugging Performance

**Step-by-step untuk optimize performance**

#### 3.1 Profile with Compose Metrics

```bash
# Build dengan compose metrics
./gradlew composeApp:assembleDebug -Pandroid.enableComposeCompilerMetrics=true
```

#### 3.2 Check Recomposition Issues

```kotlin
// ❌ WRONG: State in parent causes all children recompose
@Composable
fun Parent() {
    var counter by remember { mutableStateOf(0) }  // ❌ All children recompose

    Column {
        Counter(counter = counter)
        UnrelatedChild()  // ❌ This recomposes too!
        UnrelatedChild2()  // ❌ And this!
    }
}

// ✅ CORRECT: Hoist state appropriately
@Composable
fun Parent() {
    Column {
        CounterWithState()  // ✅ Only this recomposes
        UnrelatedChild()    // ✅ Stable
        UnrelatedChild2()   // ✅ Stable
    }
}

@Composable
private fun CounterWithState() {
    var counter by remember { mutableStateOf(0) }
    Counter(counter = counter)
}
```

#### 3.3 Check LazyColumn Performance

```kotlin
// ❌ WRONG: No keys in large lists
LazyColumn {
    items(surveys.size) { index ->
        SurveyItem(survey = surveys[index])
    }
}

// ✅ CORRECT: Stable keys for 200+ items
LazyColumn {
    items(
        items = surveys,
        key = { survey -> survey.id }  // ✅ Stable unique key
    ) { survey ->
        SurveyItem(survey = survey)
    }
}
```

#### 3.4 Check State Updates

```kotlin
// ❌ WRONG: Whole object replaced on small change
_uiState.value = _uiState.value.copy(
    // 50 fields copied...
    onlyThisFieldChanged = newValue
)

// ✅ CORRECT: Use update with minimal copying
_uiState.update { currentState ->
    currentState.copy(onlyThisFieldChanged = newValue)
}
```

**Checklist**:

- [ ] No unnecessary recompositions
- [ ] LazyColumn has stable keys
- [ ] State hoisting correct
- [ ] 60fps smooth scrolling achieved
- [ ] Memory usage acceptable

---

### Workflow 4: Fixing Security Issues

**Step-by-step untuk secure authentication**

#### 4.1 Remove Plain Text Password

```kotlin
// ❌ WRONG: Storing password
fun login(context: Context, username: String, password: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().apply {
        putString("username", username)
        putString("password", password)  // ❌ DANGER!
        apply()
    }
}

// ✅ CORRECT: Use token-based auth
fun login(context: Context, username: String, password: String) {
    // 1. Send to backend API
    val token = authenticateViaAPI(username, password)

    // 2. Store token securely
    val encryptedSharedPrefs = EncryptedSharedPreferences.create(...)
    encryptedSharedPrefs.edit().apply {
        putString("auth_token", token)
        apply()
    }
}
```

#### 4.2 Add Token Refresh Logic

```kotlin
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val securePrefs: SharedPreferences
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<AuthToken> {
        return try {
            val response = api.login(username, password)

            // Save tokens securely
            securePrefs.edit().apply {
                putString("access_token", response.accessToken)
                putString("refresh_token", response.refreshToken)
                putLong("token_expiry", response.expiryTime)
                apply()
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isTokenValid(): Boolean {
        val expiry = securePrefs.getLong("token_expiry", 0L)
        return System.currentTimeMillis() < expiry
    }

    override suspend fun refreshToken(): Result<AuthToken> {
        // Refresh logic
    }
}
```

**Checklist**:

- [ ] Password NOT stored locally
- [ ] Token-based auth implemented
- [ ] EncryptedSharedPreferences used
- [ ] Token refresh logic added
- [ ] HTTPS/TLS for all API calls
- [ ] No sensitive data in logs

---

## 🛠️ Useful Commands

### Build & Deploy

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew composeApp:assembleDebug

# Install to emulator
./gradlew composeApp:installDebug

# Run tests
./gradlew composeApp:check

# Build release APK
./gradlew composeApp:assembleRelease

# Check compilation (common code)
./gradlew compileKotlinMetadata
```

### Debugging

```bash
# View logs
adb logcat -s App

# Monitor app performance
adb shell am profile start --sampling 1000 com.app
adb shell am profile stop com.app

# Force stop app
adb shell am force-stop com.app

# Clear app data
adb shell pm clear com.app
```

### Gradle

```bash
# Refresh dependencies
./gradlew --refresh-dependencies

# Build with stack trace
./gradlew composeApp:assembleDebug --stacktrace

# Build with detailed info
./gradlew composeApp:assembleDebug --info
```

---

## 📋 Checklist: Before Committing Code

### Architecture Compliance

- [ ] No framework imports in domain layer
- [ ] Use cases properly implement business logic
- [ ] Repository interfaces in domain, implementations in data
- [ ] ViewModels only in presentation layer
- [ ] Dependency injection used correctly

### Code Quality

- [ ] No hardcoded strings for routes
- [ ] All public APIs documented with KDoc
- [ ] Naming conventions followed
- [ ] Code formatted with ktlint
- [ ] No unused imports

### Performance

- [ ] LazyColumn for 200+ items has keys
- [ ] No unnecessary state hoisting
- [ ] Recomposition scope minimal
- [ ] No objects created in hot paths
- [ ] 60fps performance target met

### Security

- [ ] No sensitive data in logs
- [ ] Passwords NOT stored locally
- [ ] Token-based auth used
- [ ] API calls use HTTPS/TLS
- [ ] Input validation implemented

### Testing

- [ ] Use case tests written
- [ ] ViewModel tests written
- [ ] Repository tests written
- [ ] UI tests for critical flows
- [ ] All tests passing

### Navigation

- [ ] Routes in sealed class, not hardcoded
- [ ] Parameters URL-encoded
- [ ] Back stack management correct
- [ ] Navigation tested manually
- [ ] No direct NavController calls in UI

### Documentation

- [ ] Architecture decisions documented
- [ ] Complex logic explained
- [ ] API usage documented
- [ ] Code comments on "why" not "what"
- [ ] README updated if needed

---

## 🔄 Commit Message Template

```
[FEATURE/BUGFIX/REFACTOR] Brief description

Detailed explanation of changes:
- Change 1
- Change 2
- Change 3

Architecture impacts:
- Layer affected
- Breaking changes (if any)

Testing:
- Tests added/updated
- Manual testing performed

Fixes: #ISSUE_NUMBER (if applicable)
```

**Example**:

```
[FEATURE] Add survey filtering by status

Added new use case GetFilteredSurveysUseCase to handle
survey filtering in the domain layer.

- Created FilterSurveysUseCase in domain
- Updated DashboardViewModel to use new use case
- Added status filter UI in DashboardScreen
- Integrated with existing StateFlow

Architecture impacts:
- New use case follows Clean Architecture pattern
- No breaking changes

Testing:
- Added unit tests for FilterSurveysUseCase
- Added ViewModel tests for status filter
- Manual testing on 200+ item list

Fixes: #456
```

---

_Last updated: May 10, 2026_
