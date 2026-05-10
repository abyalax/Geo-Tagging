# Compose UI Examples

## Screen Examples

### Dashboard Screen with LazyColumn

```kotlin
// androidMain/features/dashboard/presentation/ui/screen/DashboardScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onSurveyClick: (Survey) -> Unit,
    onSearchChange: (String) -> Unit,
    onStatusFilterChange: (SurveyStatus?) -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Field Surveys") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add new survey */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Survey"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchChange,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status Filter Chips
            StatusFilterChips(
                selectedStatus = uiState.selectedStatus,
                onStatusFilterChange = onStatusFilterChange,
                surveyStats = uiState.surveyStats,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Survey List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.surveys.isEmpty()) {
                EmptyState(
                    message = if (uiState.searchQuery.isNotEmpty()) {
                        "No surveys found matching \"${uiState.searchQuery}\""
                    } else {
                        "No surveys available"
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.surveys,
                        key = { it.id }
                    ) { survey ->
                        SurveyListItem(
                            survey = survey,
                            onClick = { onSurveyClick(survey) }
                        )
                    }
                }
            }
        }
    }
}
```

### Login Screen

```kotlin
// androidMain/features/auth/presentation/ui/screen/LoginScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Title
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Field Survey",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Username Field
        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            isError = uiState.usernameError != null,
            supportingText = uiState.usernameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password Field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.passwordError != null,
            supportingText = uiState.passwordError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Login Button
        Button(
            onClick = onLoginClick,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign In")
            }
        }
        
        // Error Message
        uiState.loginError?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
```

## Component Examples

### Survey List Item

```kotlin
// androidMain/features/dashboard/presentation/ui/component/SurveyListItem.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyListItem(
    survey: Survey,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = survey.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                
                StatusChip(status = survey.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = survey.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Location and Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "${survey.latitude}, ${survey.longitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Date
                Text(
                    text = formatDate(survey.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: SurveyStatus) {
    FilterChip(
        selected = false,
        onClick = { },
        label = { Text(status.name) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = when (status) {
                SurveyStatus.OPEN -> MaterialTheme.colorScheme.secondaryContainer
                SurveyStatus.VERIFIED -> MaterialTheme.colorScheme.primaryContainer
                SurveyStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
            },
            selectedLabelColor = when (status) {
                SurveyStatus.OPEN -> MaterialTheme.colorScheme.onSecondaryContainer
                SurveyStatus.VERIFIED -> MaterialTheme.colorScheme.onPrimaryContainer
                SurveyStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
            }
        )
    )
}
```

### Search Bar Component

```kotlin
// androidMain/features/dashboard/presentation/ui/component/SearchBar.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search surveys...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        modifier = modifier
    )
}
```

### Status Filter Chips

```kotlin
// androidMain/features/dashboard/presentation/ui/component/StatusFilterChips.kt
@Composable
fun StatusFilterChips(
    selectedStatus: SurveyStatus?,
    onStatusFilterChange: (SurveyStatus?) -> Unit,
    surveyStats: SurveyStats,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All Status
        FilterChip(
            selected = selectedStatus == null,
            onClick = { onStatusFilterChange(null) },
            label = { 
                Text("All (${surveyStats.total})")
            }
        )
        
        // Open Status
        FilterChip(
            selected = selectedStatus == SurveyStatus.OPEN,
            onClick = { onStatusFilterChange(SurveyStatus.OPEN) },
            label = { 
                Text("Open (${surveyStats.openCount})")
            }
        )
        
        // Verified Status
        FilterChip(
            selected = selectedStatus == SurveyStatus.VERIFIED,
            onClick = { onStatusFilterChange(SurveyStatus.VERIFIED) },
            label = { 
                Text("Verified (${surveyStats.verifiedCount})")
            }
        )
        
        // Rejected Status
        FilterChip(
            selected = selectedStatus == SurveyStatus.REJECTED,
            onClick = { onStatusFilterChange(SurveyStatus.REJECTED) },
            label = { 
                Text("Rejected (${surveyStats.rejectedCount})")
            }
        )
    }
}
```

### Empty State Component

```kotlin
// androidMain/features/dashboard/presentation/ui/component/EmptyState.kt
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
```

## Preview Examples

### Screen Preview

```kotlin
// androidMain/features/dashboard/presentation/ui/screen/DashboardScreen.kt
@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                surveys = listOf(
                    Survey(
                        id = "1",
                        title = "Jalan Rusak di Jakarta",
                        description = "Jalan utama mengalami kerusakan parah akibat hujan deras",
                        latitude = -6.2088,
                        longitude = 106.8456,
                        status = SurveyStatus.OPEN,
                        createdAt = Instant.now()
                    ),
                    Survey(
                        id = "2",
                        title = "Banjir di Bandung",
                        description = "Area pemukiman tergenang air setelah hujan lebat",
                        latitude = -6.9175,
                        longitude = 107.6191,
                        status = SurveyStatus.VERIFIED,
                        createdAt = Instant.now()
                    )
                ),
                surveyStats = SurveyStats(
                    total = 2,
                    openCount = 1,
                    verifiedCount = 1,
                    rejectedCount = 0
                )
            ),
            onSurveyClick = {},
            onSearchChange = {},
            onStatusFilterChange = {},
            onRefresh = {}
        )
    }
}
```

### Component Preview

```kotlin
// androidMain/features/dashboard/presentation/ui/component/SurveyListItem.kt
@Preview(showBackground = true)
@Composable
private fun SurveyListItemPreview() {
    MaterialTheme {
        SurveyListItem(
            survey = Survey(
                id = "1",
                title = "Jalan Rusak di Jakarta",
                description = "Jalan utama mengalami kerusakan parah akibat hujan deras yang terjadi selama 3 hari berturut-turut",
                latitude = -6.2088,
                longitude = 106.8456,
                status = SurveyStatus.OPEN,
                createdAt = Instant.now()
            ),
            onClick = {}
        )
    }
}
```

## Theme Examples

### Color Scheme

```kotlin
// androidMain/core/ui/theme/Color.kt
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun FieldSurveyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

_Last updated: May 10, 2026_
