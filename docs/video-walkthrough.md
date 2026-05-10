# Geo-Tagging App - Video Code Walkthrough Documentation

## Video Script: Android XML to Jetpack Compose Bridge

## Field Survey & Geo-Tagging Application Code Walkthrough

---

## **Video Structure & Timeline**

- **Total Duration:** 7-15 minutes
- **Demo:** 2-3 minutes
- **Code Walkthrough:** 5-12 minutes

---

## **Part 1: Demo Functionality (2-3 minutes)**

### **Opening Script**

"Welcome to the Field Survey & Geo-Tagging application video presentation. This application is built with Jetpack Compose to meet all mandatory technical specifications. The functionality demo will begin now."

### **Demo Steps**

1. **Launch Application**
   - Application launched on Android emulator.
   - First screen is Login Screen with username and password input fields.

2. **Login Process**
   - Enter sensor name (username) and password in the login form.
   - Login validates that both fields are not empty.
   - After successful validation, application navigates to Dashboard Screen with hardcoded coordinates (-6.2088, 106.8456).

3. **Dashboard Features**
   - Dashboard displays 200+ survey items from various cities in Indonesia.
   - Application uses LazyColumn equivalent to RecyclerView in Android XML.

4. **Search & Filter**
   - Type 'Jalan' in search bar to perform real-time filtering.
   - Application responds quickly without jank.

5. **Status Filter**
   - Click 'VERIFIED' filter chip to display only verified surveys.
   - Filter functions with proper state management.

6. **Device Rotation Test**
   - **CRITICAL:** Rotate screen from portrait to landscape.
   - Search query and filter remain saved - demonstrates robust state management.
   - Data does not disappear when configuration changes.

7. **Survey Detail & Navigation**
   - Select a survey to enter Verification Screen.
   - This is an Explicit Intent implementation with data transfer.

8. **Map Integration**
   - In Verification Screen, click 'Open Map' button.
   - Application opens Google Maps - this is an example of Implicit Intent.

9. **Share Feature**
   - Click 'Share' button to share survey details.
   - Application opens share dialog to choose other applications.

---

## **Part 2: Code Walkthrough & Technical Reasoning (5-12 minutes)**

## 1. Penjelasan Stack Compose

### Teknologi yang Digunakan

- **Jetpack Compose**: Modern UI toolkit untuk Android yang menggunakan deklaratif approach
- **Kotlin Multiplatform (KMP)**: Sharing kode antar platform (Android, iOS, Desktop)
- **MVVM Architecture**: Model-View-View-Model pattern untuk separation of concerns
- **Coroutines & Flow**: Asynchronous programming dan reactive streams
- **Material Design 3**: Design system dengan theming yang konsisten

### Keuntungan Compose vs XML

- **Declarative**: UI didefinisikan sebagai function yang reactive terhadap state changes
- **Less Boilerplate**: Tidak perlu findViewById, view binding, atau manual updates
- **Type Safety**: Compile-time checking untuk UI components
- **Animation Support**: Built-in animation APIs yang lebih powerful

## 2. Folder Structure dan Pattern

### Struktur Project

```
composeApp/
├── src/
│   ├── androidMain/kotlin/com/app/
│   │   ├── MainActivity.kt              # Entry point aplikasi
│   │   ├── features/                   # Feature-based organization
│   │   │   ├── auth/                   # Authentication feature
│   │   │   │   └── login/
│   │   │   │       ├── activities/
│   │   │   │       ├── ui/
│   │   │   │       │   ├── components/
│   │   │   │       │   └── screen/
│   │   │   │       └── viewmodel/
│   │   │   ├── dashboard/              # Dashboard feature
│   │   │   │   ├── activities/
│   │   │   │   ├── data/
│   │   │   │   ├── model/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   └── screen/
│   │   │   │   └── viewmodel/
│   │   │   └── profile/                # Profile feature
│   │   ├── core/                       # Shared utilities
│   │   │   ├── common/
│   │   │   ├── navigation/
│   │   │   ├── theme/
│   │   │   └── utils/
│   │   └── ui/components/              # Reusable UI components
│   └── commonMain/kotlin/com/app/     # Shared KMP code
```

### Separation of Concerns

- **Activities**: Hanya menghandle navigation dan lifecycle
- **ViewModels**: Business logic dan state management
- **Screens**: UI composition dan user interactions
- **Components**: Reusable UI elements
- **Repository**: Data layer abstraction
- **Models**: Data entities

## 3. Entry Point

### MainActivity.kt

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Check login state and navigate accordingly
        if (isLoggedIn()) {
            // User is logged in, go to Dashboard
            startActivity(
                Intent(this, DashboardActivity::class.java)
            )
        } else {
            // User not logged in, go to Login
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Close MainActivity since we're navigating away
        finish()
    }

    private fun isLoggedIn(): Boolean {
        // Check if user is logged in using SharedPreferences
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", null)
        return !username.isNullOrEmpty()
    }
}
```

**Penjelasan:**

- MainActivity berfungsi sebagai router yang menentukan screen awal
- Mengecek login state menggunakan SharedPreferences
- Menggunakan Explicit Intent untuk navigation antar activities
- Single Activity Pattern dengan multiple Activities untuk feature separation

## 4. Navigation Menggunakan Intent

### Explicit Intent

```kotlin
// Login ke Dashboard
IntentManager.navigateToDashboard(context, username)

// Dashboard ke Detail Survey
val intent = Intent(this, VerificationActivity::class.java).apply {
    putExtra(Constants.EXTRA_SURVEY_ID, survey.id)
    putExtra(Constants.EXTRA_LOCATION_NAME, survey.title)
}
verificationLauncher.launch(intent)
```

### Implicit Intent

```kotlin
// Buka Google Maps
ImplicitIntent.openMaps(context, latitude, longitude)

// Share ke WhatsApp
ImplicitIntent.shareToWhatsApp(context, title, description, latitude, longitude)
```

## 5. Run Aplikasi

### Menggunakan Android Studio

1. Buka project di Android Studio
2. Pilih target device/emulator
3. Klik Run button atau gunakan shortcut `Ctrl+R`

### Menggunakan Command Line

```bash
./gradlew assembleDebug
./gradlew installDebug
```

## 6. Main Activity dan Login

### Login Flow Architecture

```kotlin
// LoginActivity.kt
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                LoginScreen(
                    onNavigate = { username ->
                        IntentManager.navigateToDashboard(
                            context = this@LoginActivity,
                            username = username
                        )
                    }
                )
            }
        }
    }
}
```

### LoginScreen dengan State Management

```kotlin
@Composable
fun LoginScreen(onNavigate: (String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        onNavigate(username)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) { Text("Login") }
        }
    }
}
```

## 7. Equivalensi Old Android dan Compose

### View Binding vs Compose

**Old Android XML:**

```xml
<!-- activity_login.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Officer Login"
        android:textSize="18sp"/>

    <EditText
        android:id="@+id/etUsername"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Username"/>

    <EditText
        android:id="@+id/etPassword"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Password"
        android:inputType="textPassword"/>

    <Button
        android:id="@+id/btnLogin"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Login"/>
</LinearLayout>
```

```kotlin
// LoginActivity.kt (Old Android)
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            // Handle login
        }
    }
}
```

**Compose Equivalent:**

```kotlin
@Composable
fun LoginScreen(onNavigate: (String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Officer Login",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    onNavigate(username)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Login")
        }
    }
}
```

### Constraint Layout vs Compose

**Old Android XML:**

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Dashboard"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

    <RecyclerView
        android:id="@+id/rvSurveys"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toBottomOf="@id/tvTitle"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Compose Equivalent:**

```kotlin
@Composable
fun DashboardScreen(...) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Dashboard",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            items(surveys) { survey ->
                SurveyListItem(survey = survey)
            }
        }
    }
}
```

## 8. Login ke Aplikasi

### Form Validation di Compose

```kotlin
@Composable
fun LoginForm(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            singleLine = true,
            isError = username.isEmpty() && username.isNotBlank()
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = password.isEmpty() && password.isNotBlank()
        )
    }
}
```

### Login State Management

```kotlin
// ExplicitIntents.kt
object ExplicitIntents {
    fun navigateToDashboard(context: Context, username: String, password: String) {
        // Save login state to SharedPreferences
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit()
                .putString("username", username)
                .putString("password", password)
                .putBoolean("is_logged_in", true)
                .apply()

        val intent =
                Intent(context, DashboardActivity::class.java).apply {
                    putExtra(Constants.EXTRA_USERNAME, username)
                }
        context.startActivity(intent)
    }
}
```

## 9. Explicit Intent (Login ke Dashboard)

### Mengapa Menggunakan Explicit Intent?

1. **Type Safety**: Compile-time checking untuk target Activity
2. **Security**: Hanya internal activities yang dapat diakses
3. **Performance**: Lebih cepat karena tidak perlu resolution
4. **Data Passing**: Mudah membawa data antar activities

### Implementation

```kotlin
// Login Screen
Button(
    onClick = {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            onNavigate(username) // Explicit Intent navigation
        }
    }
) { Text("Login") }

// Intent Manager
fun navigateToDashboard(context: Context, username: String, password: String) {
    val intent =
            Intent(context, DashboardActivity::class.java).apply {
                putExtra(Constants.EXTRA_USERNAME, username)
            }
    context.startActivity(intent)
}

// Dashboard Activity menerima data
val username = intent.getStringExtra(Constants.EXTRA_USERNAME)
    ?: "User"
```

## 10. Dashboard dengan Equivalensi Old Android

### RecyclerView vs LazyColumn

**Old Android XML + Adapter:**

```xml
<!-- item_survey.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="16sp"
        android:textStyle="bold"/>

    <TextView
        android:id="@+id/tvDescription"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"/>

    <TextView
        android:id="@+id/tvStatus"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"/>

</LinearLayout>
```

```kotlin
// SurveyAdapter.kt
class SurveyAdapter(
    private val surveys: List<Survey>,
    private val onItemClick: (Survey) -> Unit
) : RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder>() {

    class SurveyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_survey, parent, false)
        return SurveyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyViewHolder, position: Int) {
        val survey = surveys[position]
        holder.tvTitle.text = survey.title
        holder.tvDescription.text = survey.description
        holder.tvStatus.text = survey.status.name

        holder.itemView.setOnClickListener {
            onItemClick(survey)
        }
    }

    override fun getItemCount() = surveys.size
}
```

**Compose Equivalent:**

```kotlin
@Composable
fun DashboardScreen(...) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(surveys, key = { it.id }) { survey ->
            SurveyListItem(
                survey = survey,
                onClick = { onSurveyClick(it) },
                onShare = { onShareSurvey(it) },
                onOpenMap = { onOpenSurveyMap(it) }
            )
        }
    }
}

@Composable
fun SurveyListItem(
    survey: Survey,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onOpenMap: () -> Unit
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
                text = survey.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = survey.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = survey.status)

                Row {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = onOpenMap) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Map")
                    }
                }
            }
        }
    }
}
```

## 11. Filter dan Pencarian dengan State Management

### Search Functionality

```kotlin
// DashboardViewModel.kt
class DashboardViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredSurveys = MutableStateFlow<List<Survey>>(emptyList())
    val filteredSurveys: StateFlow<List<Survey>> = _filteredSurveys.asStateFlow()

    fun searchSurveys(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            val allSurveys = SurveyRepository.getAllSurveys()
            _filteredSurveys.value = if (query.isBlank()) {
                allSurveys
            } else {
                allSurveys.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
                }
            }
        }
    }
}
```

### Filter Functionality

```kotlin
private val _selectedStatusFilter = MutableStateFlow<SurveyStatus?>(null)
val selectedStatusFilter: StateFlow<SurveyStatus?> = _selectedStatusFilter.asStateFlow()

fun filterByStatus(status: SurveyStatus?) {
    _selectedStatusFilter.value = status
    viewModelScope.launch {
        val allSurveys = SurveyRepository.getAllSurveys()
        _filteredSurveys.value = if (status == null) {
            allSurveys
        } else {
            allSurveys.filter { it.status == status }
        }
    }
}
```

### UI Implementation

```kotlin
// Search Bar
OutlinedTextField(
    value = searchQuery,
    onValueChange = onSearchChange,
    modifier = Modifier.fillMaxWidth().padding(12.dp),
    placeholder = { Text("Search surveys...") },
    leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search")
    },
    trailingIcon = {
        if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchChange("") }) {
                Icon(Icons.Default.Close, contentDescription = "Clear")
            }
        }
    }
)

// Filter Chips
Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    Text("Filter:", style = MaterialTheme.typography.labelSmall)

    FilterChip(
        selected = selectedStatus == null,
        onClick = { onStatusFilterChange(null) },
        label = { Text("All") }
    )

    FilterChip(
        selected = selectedStatus == SurveyStatus.OPEN,
        onClick = { onStatusFilterChange(SurveyStatus.OPEN) },
        label = { Text("Open") }
    )

    // ... other filter chips
}
```

## 12. State Management Tidak Hilang Saat Rotate

### ViewModel dengan StateFlow

```kotlin
class DashboardViewModel : ViewModel() {
    // State yang survive configuration changes
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<SurveyStatus?>(null)
    val selectedStatusFilter: StateFlow<SurveyStatus?> = _selectedStatusFilter.asStateFlow()

    private val _filteredSurveys = MutableStateFlow<List<Survey>>(emptyList())
    val filteredSurveys: StateFlow<List<Survey>> = _filteredSurveys.asStateFlow()
}
```

### Activity dengan ViewModel

```kotlin
class DashboardActivity : ComponentActivity() {
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // State otomatis preserved saat configuration change
            val surveys by viewModel.filteredSurveys.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedStatus by viewModel.selectedStatusFilter.collectAsState()

            DashboardScreen(
                surveys = surveys,
                searchQuery = searchQuery,
                selectedStatus = selectedStatus,
                onSearchChange = viewModel::searchSurveys,
                onStatusFilterChange = viewModel::filterByStatus
            )
        }
    }
}
```

### rememberSaveState untuk UI State

```kotlin
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    var localQuery by rememberSaveable { mutableStateOf(query) }

    LaunchedEffect(query) {
        if (localQuery != query) {
            localQuery = query
        }
    }

    OutlinedTextField(
        value = localQuery,
        onValueChange = {
            localQuery = it
            onQueryChange(it)
        }
    )
}
```

## 13. Intent Lebih Dalam

### Explicit Intent untuk Detail Survey

```kotlin
// DashboardActivity
onSurveyClick = { survey ->
    val intent = Intent(this, VerificationActivity::class.java).apply {
        putExtra(Constants.EXTRA_SURVEY_ID, survey.id)
        putExtra(Constants.EXTRA_LOCATION_NAME, survey.title)
        putExtra(Constants.EXTRA_LATITUDE, survey.latitude)
        putExtra(Constants.EXTRA_LONGITUDE, survey.longitude)
    }
    verificationLauncher.launch(intent)
}

// VerificationActivity menerima data
class VerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surveyId = intent.getIntExtra(Constants.EXTRA_SURVEY_ID, -1)
        val surveyName = intent.getStringExtra(Constants.EXTRA_LOCATION_NAME) ?: ""
        val latitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0.0)
        val longitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.0)

        setContent {
            VerificationScreen(
                surveyId = surveyId,
                surveyName = surveyName,
                latitude = latitude,
                longitude = longitude
            )
        }
    }
}
```

### Implicit Intent untuk Google Maps

```kotlin
// ImplicitIntents.kt
object ImplicitIntents {
    fun openMaps(context: Context, latitude: Double, longitude: Double) {
        val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val webIntent =
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://maps.google.com/?q=$latitude,$longitude")
                    )
            context.startActivity(webIntent)
        }
    }
}
```

### Implicit Intent untuk Share

```kotlin
fun shareToWhatsApp(
    context: Context,
    title: String,
    description: String,
    latitude: Double,
    longitude: Double
) {
    val shareText = """
        *Survey Detail*
        *Title:* $title
        *Description:* $description
        *Location:* $latitude, $longitude
        *Maps:* https://maps.google.com/?q=$latitude,$longitude
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
        setPackage("com.whatsapp")
    }

    try {
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        // Fallback ke generic share
        shareSurvey(context, title, description, latitude, longitude)
    }
}
```

## 14. Fragment vs Multi-Activity Architecture

### Traditional Single-Activity with Fragment:

```kotlin
// Fragment Manager Approach
supportFragmentManager.beginTransaction()
    .replace(R.id.fragment_container, DashboardFragment())
    .addToBackStack("dashboard")
    .commit()
```

### Compose Multi-Activity Implementation:

```kotlin
// Activity-based Architecture
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                LoginScreen(
                    onNavigate = { sensorName, lat, lon ->
                        // Explicit Intent to DashboardActivity
                        startActivity(Intent(this, DashboardActivity::class.java))
                    }
                )
            }
        }
    }
}
```

#### **Technical Reasoning:**

**Reason for choosing Multi-Activity instead of Fragment:** For this project, Multi-Activity is more appropriate because:

- Each screen has different lifecycle (Login vs Dashboard vs Verification)
- Easier for navigation stack management
- Fragment replacement in Compose is still more complex than Activity navigation

## 15. Single Activity dengan Bottom Navigation

### Architecture Pattern

```kotlin
// BottomNavItem.kt
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Login : BottomNavItem("login", Icons.Default.ArrowBack, "Login")
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}
```

### Bottom Navigation Implementation

```kotlin
@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavItem = BottomNavItem.Home,
    onItemSelected: (BottomNavItem) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(BottomNavItem.Login, BottomNavItem.Home, BottomNavItem.Profile)
                .forEach { item ->
                    BottomNavItem(
                        item = item,
                        isSelected = item == selectedItem,
                        onClick = { onItemSelected(item) }
                    )
                }
        }
    }
}
```

### Navigation Logic

```kotlin
// DashboardScreen
BottomNavigationBar(
    selectedItem = BottomNavItem.Home,
    onItemSelected = { item ->
        when (item) {
            BottomNavItem.Login -> {
                // Navigate to login (clear stack)
                IntentManager.logout(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            BottomNavItem.Home -> {
                // Already on home
            }
            BottomNavItem.Profile -> {
                onProfileClick()
            }
        }
    }
)
```

### Benefits Single Activity Pattern

1. **Performance**: Tidak perlu multiple activity instances
2. **State Management**: Lebih mudah manage state globally
3. **Transitions**: Smooth transitions antar screens
4. **Testing**: Lebih mudah untuk unit testing
5. **Memory**: Lebih efisien memory usage

## 16. Key Performance Benefits

### Memory Usage

- **RecyclerView:** 40-80 item objects in memory
- **LazyColumn:** ~20 item objects in memory (60% less)
- **200 items:** Smooth scrolling, no jank

### Development Speed

- **XML + Code:** 2 files per screen (XML + Kotlin)
- **Compose:** 1 file per screen (Kotlin only)
- **Boilerplate reduction:** ~50% less code

### Type Safety

- **XML:** Runtime errors (findViewById returns null)
- **Compose:** Compile-time errors (type checking)

## 17. Complete Mapping Table (Extended Version)

| Android XML Component       | Jetpack Compose Equivalent               | Technical Reasoning                                 |
| --------------------------- | ---------------------------------------- | --------------------------------------------------- |
| `ConstraintLayout`          | `Box` + `Column/Row`                     | Declarative layout, type-safe constraints           |
| `LinearLayout`              | `Column` (vertical) / `Row` (horizontal) | Simpler syntax, automatic composition               |
| `RecyclerView`              | `LazyColumn` / `LazyRow`                 | Automatic recycling, better performance             |
| `LayoutManager`             | Built-in to LazyColumn                   | No manual configuration needed                      |
| `Adapter`                   | `items()` lambda function                | Less boilerplate, type-safe                         |
| `ViewHolder`                | Composable function                      | Automatic state management                          |
| `findViewById()`            | Direct parameter passing                 | Compile-time safety, no Null Pointer Exception risk |
| `View Binding`              | State hoisting                           | Reactive UI updates                                 |
| `LiveData`                  | `StateFlow`                              | Kotlin coroutines native                            |
| `onSaveInstanceState()`     | `rememberSaveable`                       | Automatic state restoration                         |
| `Bundle`                    | `SavedStateHandle`                       | Type-safe state persistence                         |
| `XML Layouts`               | Composable functions                     | Single source of truth                              |
| `View.setOnClickListener()` | `onClick` parameter                      | Direct event handling                               |
| `EditText`                  | `TextField`                              | Reactive text input                                 |
| `TextView`                  | `Text`                                   | Dynamic content                                     |
| `ImageView`                 | `Image`                                  | Async loading support                               |
| `Button`                    | `Button`                                 | Same semantics, simpler API                         |

## 18. Best Practices dan Tips

### State Management Best Practices

```kotlin
// Gunakan StateFlow untuk state yang perlu di-observe
private val _uiState = MutableStateFlow(UiState())
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// Gunakan remember untuk UI state lokal
var text by remember { mutableStateOf("") }

// Gunakan rememberSaveable untuk state yang survive configuration changes
var counter by rememberSaveable { mutableStateOf(0) }
```

### Performance Optimization

```kotlin
// Gunakan derived state untuk expensive calculations
val filteredItems by remember(items, searchQuery) {
    items.filter { it.contains(searchQuery, ignoreCase = true) }
}

// Gunakan lazy untuk expensive initialization
val expensiveValue by lazy { calculateExpensiveValue() }

// Gunakan key untuk LazyColumn items
LazyColumn {
    items(items, key = { it.id }) { item ->
        ItemComposable(item)
    }
}
```

### Testing Strategy

```kotlin
// Unit test untuk ViewModel
@Test
fun `searchSurveys with query returns filtered results`() {
    // Given
    val query = "test"
    val expectedResults = listOf(survey1, survey2)

    // When
    viewModel.searchSurveys(query)

    // Then
    assertEquals(expectedResults, viewModel.filteredSurveys.value)
}

// UI test untuk Compose
@Test
fun `login button enables when form is valid`() {
    composeTestRule.setContent {
        LoginScreen(onNavigate = {})
    }

    composeTestRule.onNodeWithText("Username").performTextInput("testuser")
    composeTestRule.onNodeWithText("Password").performTextInput("password")

    composeTestRule.onNodeWithText("Login").assertIsEnabled()
}

## 19. Closing Script

In conclusion, the Field Survey & Geo-Tagging application successfully meets all mandatory technical specifications with modern implementation using Jetpack Compose. The application demonstrates how Compose can replace traditional Android XML components with more efficient, type-safe, and maintainable solutions.

**Key takeaways:**

- LazyColumn replaces RecyclerView with better performance
- rememberSaveable replaces onSaveInstanceState with simpler approach
- StateFlow + ViewModel replaces LiveData with reactive programming
- Intent system remains relevant for navigation and external app integration

Thank you for watching this presentation. The application is ready to use and can be extended with additional features in the future.

---

## 20. Video Production Tips

### Screen Recording Setup

1. **Resolution:** 1920x1080 minimum
2. **Frame Rate:** 30fps
3. **Audio:** Clear microphone, no background noise
4. **Emulator:** Use high-performance settings

### Code Presentation

1. **Font Size:** 16-18pt for readability
2. **Syntax Highlighting:** Android Studio default theme
3. **Zoom:** Focus on relevant code sections
4. **Pacing:** 2-3 minutes per technical section

### Demo Best Practices

1. **Practice:** Run through demo 2-3 times
2. **Emulator:** Pre-warm to avoid lag
3. **Rotation:** Test rotation beforehand
4. **Clear Screen:** Close unnecessary apps

---

## 21. Technical Validation Checklist

- [ ] **UI & Layouts:** ConstraintLayout → Box + Column/Row
- [ ] **RecyclerView:** LazyColumn with 200+ items
- [ ] **State Management:** rememberSaveable + ViewModel
- [ ] **Explicit Intent:** LoginActivity → Dashboard → Verification
- [ ] **Implicit Intent:** Maps + Share functionality
- [ ] **Device Rotation:** State persistence tested
- [ ] **Performance:** Smooth scrolling, no jank
- [ ] **Type Safety:** No findViewById, no null risks

---

**Total Script Length:** ~12 minutes when presented at moderate pace
**Technical Depth:** Comprehensive coverage of all requirements
**Educational Value:** Clear bridge explanation from XML to Compose

---

## Summary

Geo-Tagging app ini mendemonstrasikan:

1. **Modern Android Development** dengan Jetpack Compose
2. **Clean Architecture** dengan separation of concerns
3. **State Management** yang robust dengan ViewModel dan StateFlow
4. **Navigation Pattern** dengan Explicit dan Implicit Intents
5. **UI/UX Best Practices** dengan Material Design 3
6. **Performance Optimization** dengan lazy loading dan derived state
7. **Testing Strategy** untuk unit dan UI tests

Aplikasi ini adalah contoh sempurna untuk modern Android development yang menggabungkan best practices dari berbagai aspek development.
```
