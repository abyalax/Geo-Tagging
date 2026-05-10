# Geo-Tagging App Code Walkthrough

## 📱 Penjelasan Stack Compose

### Jetpack Compose Architecture

Geo-Tagging application menggunakan **Jetpack Compose** sebagai UI framework modern yang menggantikan sistem View XML tradisional. Compose menggunakan pendekatan **declarative UI** dimana UI didefinisikan sebagai fungsi Kotlin yang bereaksi terhadap perubahan state.

```kotlin
// Traditional XML View (Old Android)
<TextView
    android:id="@+id/title"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Hello World" />

// Compose Equivalent (Modern)
@Composable
fun TitleText() {
    Text(text = "Hello World")
}
```

### Key Compose Concepts

1. **Composable Functions**: Fungsi yang diannotasi dengan `@Composable`
2. **State Management**: Menggunakan `remember` dan `mutableStateOf`
3. **Recomposition**: Otomatis update UI ketika state berubah
4. **Modifiers**: Mengatur tampilan dan behavior komponen

---

## 📁 Folder Structure dan Pattern

### Clean Architecture + Feature-Based Structure

```
composeApp/src/
├── commonMain/kotlin/                    # Shared business logic
│   ├── core/
│   │   ├── domain/                       # Pure business logic
│   │   │   ├── model/                    # Domain entities
│   │   │   ├── repository/               # Repository interfaces
│   │   │   └── usecase/                  # Business use cases
│   │   └── common/                       # Shared utilities
│   └── features/
│       └── [feature]/
│           ├── domain/                   # Feature-specific domain
│           └── presentation/             # Shared presentation logic
├── androidMain/kotlin/                   # Android-specific implementation
│   ├── core/
│   │   └── data/                         # Repository implementations
│   └── features/
│       └── [feature]/
│           └── presentation/
│               ├── ui/
│               │   ├── screen/           # Screen composables
│               │   └── component/        # Reusable components
│               └── viewmodel/            # ViewModels
```

### Separation of Concerns

1. **Domain Layer** (`commonMain/core/domain/`)
   - Pure Kotlin, no framework dependencies
   - Business entities and rules
   - Repository interfaces
   - Use cases

2. **Data Layer** (`androidMain/core/data/`)
   - Repository implementations
   - Data sources (local/remote)
   - Mappers between data and domain models

3. **Presentation Layer** (`androidMain/features/*/presentation/`)
   - ViewModels with StateFlow
   - UI composables
   - Navigation logic

---

## 🚀 Entry Point

### Application Entry Point

```kotlin
// App.kt
@Composable
fun App() {
    ApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                Text(
                    text = "Hello KMP!",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Action")
                }
            }
        }
    }
}
```

### MainActivity sebagai Single Entry Point

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent { ApplicationTheme { AppNavHost() } }
    }
}
```

**Kenapa Single Activity?**

- Performa lebih baik dengan satu Activity
- Navigation lebih konsisten menggunakan Compose Navigation
- State management lebih sederhana
- Lifecycle management lebih mudah

---

## 🧭 Navigation Menggunakan Intent

### Explicit Intent (Internal Navigation)

```kotlin
// Dari Login ke Dashboard
val intent = Intent(this, DashboardActivity::class.java)
startActivity(intent)

// Dengan membawa data
val intent = Intent(this, SurveyDetailActivity::class.java).apply {
    putExtra("SURVEY_ID", surveyId)
    putExtra("SURVEY_NAME", surveyName)
}
startActivity(intent)
```

### Implicit Intent (External Apps)

```kotlin
// Membuka Google Maps dengan koordinat
val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
mapIntent.setPackage("com.google.android.apps.maps")
startActivity(mapIntent)

// Membuka WhatsApp
val intent = Intent(Intent.ACTION_VIEW).apply {
    data = Uri.parse("https://wa.me/$phoneNumber?text=$message")
}
startActivity(intent)
```

---

## 🔧 Run Aplikasi Menggunakan Extension

### Android Studio Extension

```bash
# Build dan install debug APK
./gradlew composeApp:installDebug

# Run dengan hot reload
./gradlew composeApp:installDebug --continue

# Build release APK
./gradlew composeApp:assembleRelease
```

### Configuration untuk Development

```kotlin
// build.gradle.kts
android {
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            debuggable = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}
```

---

## 🏠 MainActivity dan Login

### MainActivity Structure

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GeoTaggingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
```

### Login Screen Implementation

```kotlin
// LoginScreen.kt
@Composable
fun LoginScreen(onNavigateToDashboard: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content with weight
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Officer Login", style = MaterialTheme.typography.headlineSmall)

                LoginForm(
                    username = username,
                    onUsernameChange = { username = it },
                    password = password,
                    onPasswordChange = { password = it }
                )

                Button(
                    onClick = {
                        Log.d("LoginScreen", "Login button clicked")
                        Log.d("LoginScreen", "Username: '$username', Password: '$password'")
                        // Simple validation
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            Log.d("LoginScreen", "Validation passed, calling onNavigateToDashboard")
                            onNavigateToDashboard(username, password)
                        } else {
                            Log.d("LoginScreen", "Validation failed - empty username or password")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Login") }
            }

            // Bottom Navigation
            BottomNavigationBar(
                selectedItem = BottomNavItem.Login,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.Login -> {
                            // Already on login
                        }
                        BottomNavItem.Home -> {
                            // Navigate to dashboard (home) - but only if logged in
                            if (username.isNotEmpty() && password.isNotEmpty()) {
                                onNavigateToDashboard(username, password)
                            }
                        }
                        BottomNavItem.Profile -> {
                            // Can't access profile without login
                        }
                    }
                }
            )
        }
    }
}
```

---

## 🔄 Equivalensi Old Android dan Compose

### View Binding → Compose

```kotlin
// Old Android dengan View Binding
private lateinit var binding: ActivityLoginBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.usernameEditText.setText("user")
    binding.loginButton.setOnClickListener { /* handle login */ }
}

// Compose Equivalent
@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        Button(onClick = { /* handle login */ }) {
            Text("Login")
        }
    }
}
```

### Constraint Layout & Linear Layout → Compose

```xml
<!-- Old Android XML -->
<LinearLayout
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView android:layout_width="wrap_content" />
    <Button android:layout_width="match_parent" />
</LinearLayout>
```

```kotlin
// Compose Equivalent
@Composable
fun LoginLayout() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = "Welcome")
        Button(
            onClick = { /* */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}
```

---

## 🔐 Login ke Aplikasi

### Login Flow Implementation

```kotlin
// LoginViewModel.kt
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            loginUseCase(
                username = uiState.value.username,
                password = uiState.value.password
            ).fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
            )
        }
    }
}
```

### Input Validation

```kotlin
// Validation logic
fun validateLoginInput(username: String, password: String): LoginValidationResult {
    return when {
        username.isBlank() -> LoginValidationResult.Error("Username tidak boleh kosong")
        password.isBlank() -> LoginValidationResult.Error("Password tidak boleh kosong")
        password.length < 6 -> LoginValidationResult.Error("Password minimal 6 karakter")
        else -> LoginValidationResult.Success
    }
}
```

---

## 🎯 Explicit Intent (Login ke Dashboard)

### Implementasi Explicit Intent

```kotlin
// AppNavHost.kt
@Composable
fun AppNavHost(
        navController: NavHostController = rememberNavController(),
        startDestination: String = Routes.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Splash.route) { SplashScreen(navController = navController) }

        composable(Routes.Login.route) {
            val context = LocalContext.current
            LoginScreen(
                    onNavigateToDashboard = { username, password ->
                        Log.d("AppNavHost", "Login navigation callback triggered")
                        Log.d("AppNavHost", "Username: '$username', Password: '$password'")
                        // Save login state and navigate
                        Log.d("AppNavHost", "Calling AuthMiddleware.login")
                        AuthMiddleware.login(context, username, password)
                        Log.d(
                                "AppNavHost",
                                "Navigating to Dashboard route: ${Routes.Dashboard.route}"
                        )
                        navController.navigate(Routes.Dashboard.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                        Log.d("AppNavHost", "Navigation command executed")
                    }
            )
        }

        composable(Routes.Dashboard.route) {
            val context = LocalContext.current
            val username = AuthMiddleware.getUsername(context) ?: "Field Officer"
            val viewModel: DashboardViewModel = viewModel()

            // Collect StateFlow properly
            val surveys by viewModel.filteredSurveys.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
            val surveyStats = viewModel.getSurveyStats()

            DashboardScreen(
                    surveys = surveys,
                    isLoading = isLoading,
                    onSurveyClick = { survey ->
                        navController.navigate(
                                Routes.Verification.createRoute(survey.id.toString(), survey.title)
                        )
                    },
                    onShareSurvey = { survey ->
                        IntentNavigation.shareSurveyViaWhatsApp(context, survey)
                    },
                    onOpenSurveyMap = { survey ->
                        IntentNavigation.openSurveyInGoogleMaps(context, survey)
                    },
                    searchQuery = searchQuery,
                    onSearchChange = viewModel::searchSurveys,
                    selectedStatus = selectedStatus,
                    onStatusFilterChange = viewModel::filterByStatus,
                    username = username,
                    onNavigateToProfile = { navController.navigate(Routes.Profile.route) },
                    onNavigateToVerification = { surveyId, locationName ->
                        navController.navigate(
                                Routes.Verification.createRoute(surveyId, locationName)
                        )
                    },
                    onNavigateToLogin = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Dashboard.route) { inclusive = true }
                        }
                    },
                    onLogout = { AuthMiddleware.logout(context, navController) },
                    surveyStats = surveyStats
            )
        }
    }
}
```

### Alasan Menggunakan Explicit Intent

1. **Type Safety**: Compile-time checking untuk destination
2. **Data Passing**: Aman untuk membawa data antar screen
3. **Back Stack Management**: Kontrol penuh atas navigation stack
4. **Performance**: Lebih efisien daripada implicit intent

---

## 📊 Dashboard: LayoutManager, Adapter, ViewHolder Equivalency

### Old Android RecyclerView Pattern

```kotlin
// Adapter
class SurveyAdapter : RecyclerView.Adapter<SurveyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_survey, parent, false)
        return SurveyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyViewHolder, position: Int) {
        holder.bind(surveys[position])
    }

    override fun getItemCount() = surveys.size
}

// ViewHolder
class SurveyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(survey: Survey) {
        itemView.titleTextView.text = survey.name
        itemView.statusTextView.text = survey.status
    }
}

// LayoutManager
recyclerView.layoutManager = LinearLayoutManager(this)
recyclerView.adapter = SurveyAdapter()
```

### Compose LazyColumn Equivalent

```kotlin
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = uiState.surveys,
            key = { survey -> survey.id } // Stable key untuk performance
        ) { survey ->
            SurveyItem(
                survey = survey,
                onClick = { /* handle click */ }
            )
        }
    }
}

@Composable
private fun SurveyItem(
    survey: Survey,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = survey.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = survey.status,
                style = MaterialTheme.typography.bodySmall,
                color = when (survey.status) {
                    "Completed" -> MaterialTheme.colorScheme.primary
                    "Pending" -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
```

### Performance Optimization

```kotlin
// LazyColumn dengan optimization untuk 200+ items
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    state = rememberLazyListState(), // Untuk scroll position
    flingBehavior = ScrollableDefaults.flingBehavior() // Smooth scrolling
) {
    items(
        items = surveys,
        key = { it.id }, // Penting untuk performance!
        contentType = { it.javaClass } // Untuk composition caching
    ) { survey ->
        // Stable composable untuk mencegah unnecessary recomposition
        SurveyItem(
            survey = survey,
            modifier = Modifier.animateItemPlacement() // Smooth animations
        )
    }
}
```

---

## 🔍 Filter dan Pencarian

### Search Implementation

```kotlin
// DashboardViewModel.kt
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getSurveysUseCase: GetSurveysUseCase,
    private val filterSurveysUseCase: FilterSurveysUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterSurveys()
    }

    fun onStatusFilterChanged(status: String) {
        _uiState.update { it.copy(statusFilter = status) }
        filterSurveys()
    }

    private fun filterSurveys() {
        val filtered = filterSurveysUseCase(
            surveys = _uiState.value.allSurveys,
            searchQuery = _uiState.value.searchQuery,
            statusFilter = _uiState.value.statusFilter
        )
        _uiState.update { it.copy(filteredSurveys = filtered) }
    }
}
```

### Search UI

```kotlin
@Composable
private fun SearchAndFilterBar(
    searchQuery: String,
    statusFilter: String,
    onSearchQueryChanged: (String) -> Unit,
    onStatusFilterChanged: (String) -> Unit
) {
    Column {
        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text("Cari survey...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("All", "Completed", "Pending", "In Progress")) { status ->
                FilterChip(
                    selected = statusFilter == status,
                    onClick = { onStatusFilterChanged(status) },
                    label = { Text(status) }
                )
            }
        }
    }
}
```

---

## 💾 State Management & Rotation Handling

### State Persistence

```kotlin
// ViewModel dengan StateFlow untuk automatic rotation handling
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // State yang survive rotation
    private val _searchQuery = savedStateHandle.getStateFlow("search_query", "")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _statusFilter = savedStateHandle.getStateFlow("status_filter", "All")
    val statusFilter: StateFlow<String> = _statusFilter

    fun onSearchQueryChanged(query: String) {
        savedStateHandle["search_query"] = query
    }

    fun onStatusFilterChanged(status: String) {
        savedStateHandle["status_filter"] = status
    }
}
```

### rememberSaveable untuk Compose State

```kotlin
@Composable
fun SearchScreen() {
    // State yang survive configuration changes
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        label = { Text("Search") }
    )

    // State ini akan tetap ada saat rotation
    LaunchedEffect(searchQuery) {
        // Perform search
    }
}
```

### ViewModel State Management

```kotlin
data class DashboardUiState(
    val surveys: List<Survey> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    // Derived state untuk filtered surveys
    val filteredSurveys: List<Survey>
        get() = surveys.filter { survey ->
            val matchesSearch = survey.name.contains(searchQuery, ignoreCase = true)
            val matchesStatus = statusFilter == "All" || survey.status == statusFilter
            matchesSearch && matchesStatus
        }
}
```

---

## 🧭 Penjelasan Intent Lebih Dalam

### Explicit Intent untuk Detail Page

````kotlin
// Dari list ke detail dengan data
@Composable
fun SurveyListScreen(
    onNavigateToDetail: (String, String) -> Unit
) {
    LazyColumn {
        items(surveys) { survey ->
            SurveyItem(
                survey = survey,
                onClick = {
                    // Explicit intent dengan data
                    onNavigateToDetail(
                        survey.id,
                        survey.name
                    )
                }
            )
        }
    }
}

### Navigation setup - Routes.kt

```kotlin
// Routes.kt
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

### Implicit Intent untuk External Apps

```kotlin
// IntentNavigation.kt
object IntentNavigation {

    /** Share survey information via WhatsApp */
    fun shareSurveyViaWhatsApp(context: Context, survey: Survey) {
        val shareText =
                """
            📍 Survey Report
            📍 Lokasi: ${survey.title}
            📝 Deskripsi: ${survey.description}
            🗺️ Koordinat: ${survey.latitude}, ${survey.longitude}
            📊 Status: ${survey.status.name}

            Lihat lokasi di Google Maps:
            https://maps.google.com/?q=${survey.latitude},${survey.longitude}
        """.trimIndent()

        val intent =
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    setPackage("com.whatsapp")
                }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general share if WhatsApp not installed
            shareSurveyGeneral(context, survey, shareText)
        }
    }

    /** Open survey location in Google Maps */
    fun openSurveyInGoogleMaps(context: Context, survey: Survey) {
        val intent =
                Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                        "geo:${survey.latitude},${survey.longitude}?q=${survey.latitude},${survey.longitude}(${survey.title})"
                                )
                        )
                        .apply { setPackage("com.google.android.apps.maps") }
        context.startActivity(intent)
    }

    /** General share functionality for fallback */
    private fun shareSurveyGeneral(context: Context, survey: Survey, shareText: String) {
        val fallbackIntent =
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    putExtra(Intent.EXTRA_SUBJECT, "Survey Report: ${survey.title}")
                }
        context.startActivity(Intent.createChooser(fallbackIntent, "Bagikan Survey"))
    }
}
````

### Intent Security Best Practices

```kotlin
// Secure intent handling
private fun navigateToDetail(surveyId: String) {
    try {
        // Validate input
        require(surveyId.isNotBlank()) { "Survey ID cannot be empty" }

        // Sanitize input
        val sanitizedId = surveyId.trim().take(50)

        val intent = Intent(this, SurveyDetailActivity::class.java).apply {
            putExtra("SURVEY_ID", sanitizedId)
            // Gunakan Bundle untuk data kompleks
            putExtra("SURVEY_DATA", Bundle().apply {
                putString("id", sanitizedId)
                putLong("timestamp", System.currentTimeMillis())
            })
        }

        startActivity(intent)
    } catch (e: Exception) {
        // Handle error gracefully
        showError("Invalid survey data")
    }
}
```

---

## 🏗️ Single Activity Architecture dengan Bottom Navigation

### Architecture Overview

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent { ApplicationTheme { AppNavHost() } }
    }
}
```

### Bottom Navigation Implementation

```kotlin
sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavScreen(
        route = "dashboard",
        title = "Dashboard",
        icon = Icons.Default.Dashboard
    )

    object Surveys : BottomNavScreen(
        route = "surveys",
        title = "Surveys",
        icon = Icons.Default.List
    )

    object Profile : BottomNavScreen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        BottomNavScreen.values().forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    // Navigate ke top of stack untuk menghindari duplikasi
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
```

### State Management Across Bottom Navigation

```kotlin
// ViewModel yang shared across bottom navigation screens
@HiltViewModel
class SharedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // User state yang persist across navigation
    private val _userState = MutableStateFlow<UserState?>(null)
    val userState: StateFlow<UserState?> = _userState.asStateFlow()

    // Notification count
    private val _notificationCount = MutableStateFlow(0)
    val notificationCount: StateFlow<Int> = _notificationCount.asStateFlow()

    fun updateUserState(user: UserState) {
        _userState.value = user
    }

    fun incrementNotificationCount() {
        _notificationCount.value++
    }
}

// Menggunakan shared ViewModel di screens
@Composable
fun DashboardScreen(
    sharedViewModel: SharedViewModel = viewModel()
) {
    val userState by sharedViewModel.userState.collectAsState()
    val notificationCount by sharedViewModel.notificationCount.collectAsState()

    // UI yang menggunakan shared state
    TopAppBar(
        title = { Text("Dashboard") },
        actions = {
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge { Text(notificationCount.toString()) }
                    }
                }
            ) {
                IconButton(onClick = { /* */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }
        }
    )
}
```

### Benefits of Single Activity Architecture

1. **Performance**: Hanya satu instance Activity
2. **State Management**: Lebih mudah dengan shared ViewModels
3. **Navigation**: Konsisten dengan Compose Navigation
4. **Lifecycle**: Lebih sederhana untuk diprediksi
5. **Testing**: Lebih mudah untuk di-test
6. **Memory**: Lebih efisien dalam penggunaan memory

---

## 📝 Summary

Geo-Tagging application mengimplementasikan:

- **Modern UI**: Jetpack Compose dengan declarative approach
- **Clean Architecture**: Separation of concerns yang jelas
- **Single Activity**: Performa optimal dengan navigation yang konsisten
- **State Management**: ViewModel + StateFlow untuk rotation-safe state
- **Navigation**: Explicit dan implicit intents untuk internal dan external navigation
- **Performance**: LazyColumn optimization untuk 200+ items
- **Security**: Proper validation dan secure intent handling

Arsitektur ini memastikan aplikasi yang scalable, maintainable, dan performant untuk kebutuhan survey geo-tagging yang kompleks.
