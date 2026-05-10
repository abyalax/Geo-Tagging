# Clean Architecture Examples

## Domain Layer Examples

### Use Case Implementation

```kotlin
// commonMain/core/domain/usecase/GetSurveysUseCase.kt
class GetSurveysUseCase @Inject constructor(
    private val surveyRepository: SurveyRepository
) {
    suspend operator fun invoke(
        searchQuery: String = "",
        statusFilter: SurveyStatus? = null
    ): Result<List<Survey>> {
        return try {
            val surveys = surveyRepository.getSurveys()
            val filteredSurveys = surveys.filter { survey ->
                val matchesSearch = searchQuery.isEmpty() || 
                    survey.title.contains(searchQuery, ignoreCase = true) ||
                    survey.description.contains(searchQuery, ignoreCase = true)
                
                val matchesStatus = statusFilter == null || survey.status == statusFilter
                
                matchesSearch && matchesStatus
            }
            Result.success(filteredSurveys)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Repository Interface

```kotlin
// commonMain/core/domain/repository/SurveyRepository.kt
interface SurveyRepository {
    suspend fun getSurveys(): List<Survey>
    suspend fun getSurveyById(id: String): Survey?
    suspend fun updateSurvey(survey: Survey): Result<Survey>
    suspend fun getSurveyStats(): SurveyStats
}
```

### Domain Model

```kotlin
// commonMain/core/domain/model/Survey.kt
data class Survey(
    val id: String,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val status: SurveyStatus,
    val createdAt: Instant,
    val verifiedAt: Instant? = null,
    val verifiedBy: String? = null
)

enum class SurveyStatus {
    OPEN,
    VERIFIED,
    REJECTED
}
```

## Data Layer Examples

### Repository Implementation

```kotlin
// androidMain/core/data/repository/SurveyRepositoryImpl.kt
class SurveyRepositoryImpl @Inject constructor(
    private val localDataSource: SurveyLocalDataSource,
    private val remoteDataSource: SurveyRemoteDataSource,
    private val surveyMapper: SurveyMapper
) : SurveyRepository {

    override suspend fun getSurveys(): List<Survey> {
        return try {
            // Try remote first, fallback to local
            val remoteSurveys = remoteDataSource.getSurveys()
            localDataSource.cacheSurveys(remoteSurveys)
            remoteSurveys.map { surveyMapper.toDomain(it) }
        } catch (e: Exception) {
            // Fallback to cached data
            val localSurveys = localDataSource.getSurveys()
            localSurveys.map { surveyMapper.toDomain(it) }
        }
    }

    override suspend fun updateSurvey(survey: Survey): Result<Survey> {
        return try {
            val surveyEntity = surveyMapper.toEntity(survey)
            val updatedSurvey = remoteDataSource.updateSurvey(surveyEntity)
            localDataSource.updateSurvey(updatedSurvey)
            Result.success(surveyMapper.toDomain(updatedSurvey))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Mapper Implementation

```kotlin
// androidMain/core/data/mapper/SurveyMapper.kt
class SurveyMapper {
    
    fun toDomain(entity: SurveyEntity): Survey {
        return Survey(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            latitude = entity.latitude,
            longitude = entity.longitude,
            status = SurveyStatus.valueOf(entity.status),
            createdAt = Instant.parse(entity.createdAt),
            verifiedAt = entity.verifiedAt?.let { Instant.parse(it) },
            verifiedBy = entity.verifiedBy
        )
    }
    
    fun toEntity(domain: Survey): SurveyEntity {
        return SurveyEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            latitude = domain.latitude,
            longitude = domain.longitude,
            status = domain.status.name,
            createdAt = domain.createdAt.toString(),
            verifiedAt = domain.verifiedAt?.toString(),
            verifiedBy = domain.verifiedBy
        )
    }
}
```

## Presentation Layer Examples

### ViewModel Implementation

```kotlin
// androidMain/features/dashboard/presentation/viewmodel/DashboardViewModel.kt
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getSurveysUseCase: GetSurveysUseCase,
    private val updateSurveyUseCase: UpdateSurveyUseCase,
    private val getSurveyStatsUseCase: GetSurveyStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadSurveys()
        loadSurveyStats()
    }

    private fun loadSurveys() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            getSurveysUseCase(
                searchQuery = _uiState.value.searchQuery,
                statusFilter = _uiState.value.selectedStatus
            ).fold(
                onSuccess = { surveys ->
                    _uiState.update { 
                        it.copy(
                            surveys = surveys,
                            isLoading = false
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

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadSurveys()
    }

    fun onStatusFilterChanged(status: SurveyStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        loadSurveys()
    }

    fun onSurveyVerified(surveyId: String) {
        viewModelScope.launch {
            val currentSurvey = _uiState.value.surveys.find { it.id == surveyId }
            currentSurvey?.let { survey ->
                updateSurveyUseCase(survey.copy(status = SurveyStatus.VERIFIED))
                    .fold(
                        onSuccess = { loadSurveys() },
                        onFailure = { error ->
                            _uiState.update { 
                                it.copy(error = error.message)
                            }
                        }
                    )
            }
        }
    }
}
```

### UI State Implementation

```kotlin
// androidMain/features/dashboard/presentation/model/DashboardUiState.kt
data class DashboardUiState(
    val surveys: List<Survey> = emptyList(),
    val surveyStats: SurveyStats = SurveyStats(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedStatus: SurveyStatus? = null,
    val error: String? = null
)
```

## Navigation Examples

### Routes Definition

```kotlin
// commonMain/core/navigation/Routes.kt
sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Dashboard : Routes("dashboard")
    object Verification : Routes("verification/{surveyId}") {
        fun createRoute(surveyId: String) = "verification/$surveyId"
    }
    object Profile : Routes("profile")
}
```

### Navigation Manager

```kotlin
// androidMain/core/navigation/NavigationManager.kt
class NavigationManager @Inject constructor(
    private val navController: NavController
) {
    
    fun navigateToLogin() {
        navController.navigate(Routes.Login.route) {
            popUpTo(Routes.Dashboard.route) { inclusive = true }
        }
    }
    
    fun navigateToDashboard() {
        navController.navigate(Routes.Dashboard.route) {
            popUpTo(Routes.Login.route) { inclusive = true }
        }
    }
    
    fun navigateToVerification(surveyId: String) {
        navController.navigate(Routes.Verification.createRoute(surveyId))
    }
    
    fun navigateToProfile() {
        navController.navigate(Routes.Profile.route)
    }
    
    fun navigateBack() {
        navController.popBackStack()
    }
}
```

## Testing Examples

### Use Case Test

```kotlin
// commonTest/core/domain/usecase/GetSurveysUseCaseTest.kt
class GetSurveysUseCaseTest {
    
    private lateinit var repository: FakeSurveyRepository
    private lateinit var getSurveysUseCase: GetSurveysUseCase
    
    @Before
    fun setup() {
        repository = FakeSurveyRepository()
        getSurveysUseCase = GetSurveysUseCase(repository)
    }
    
    @Test
    fun `should return all surveys when no filters applied`() = runTest {
        // Given
        val expectedSurveys = repository.getSurveys()
        
        // When
        val result = getSurveysUseCase()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedSurveys, result.getOrNull())
    }
    
    @Test
    fun `should filter surveys by search query`() = runTest {
        // Given
        val searchQuery = "Jalan"
        
        // When
        val result = getSurveysUseCase(searchQuery = searchQuery)
        
        // Then
        assertTrue(result.isSuccess)
        val filteredSurveys = result.getOrNull()
        assertTrue(filteredSurveys!!.all { 
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        })
    }
    
    @Test
    fun `should filter surveys by status`() = runTest {
        // Given
        val statusFilter = SurveyStatus.VERIFIED
        
        // When
        val result = getSurveysUseCase(statusFilter = statusFilter)
        
        // Then
        assertTrue(result.isSuccess)
        val filteredSurveys = result.getOrNull()
        assertTrue(filteredSurveys!!.all { it.status == statusFilter })
    }
}
```

### ViewModel Test

```kotlin
// androidTest/features/dashboard/presentation/viewmodel/DashboardViewModelTest.kt
@ExperimentalCoroutinesApi
class DashboardViewModelTest {
    
    private lateinit var viewModel: DashboardViewModel
    private lateinit var getSurveysUseCase: GetSurveysUseCase
    private lateinit var updateSurveyUseCase: UpdateSurveyUseCase
    private lateinit var getSurveyStatsUseCase: GetSurveyStatsUseCase
    
    @Before
    fun setup() {
        getSurveysUseCase = mockk()
        updateSurveyUseCase = mockk()
        getSurveyStatsUseCase = mockk()
        
        coEvery { getSurveysUseCase(any(), any()) } returns Result.success(emptyList())
        coEvery { getSurveyStatsUseCase() } returns SurveyStats()
        coEvery { updateSurveyUseCase(any()) } returns Result.success(mockk())
        
        viewModel = DashboardViewModel(
            getSurveysUseCase = getSurveysUseCase,
            updateSurveyUseCase = updateSurveyUseCase,
            getSurveyStatsUseCase = getSurveyStatsUseCase
        )
    }
    
    @Test
    fun `should load surveys on initialization`() = runTest {
        // Given
        val surveys = listOf(
            Survey(
                id = "1",
                title = "Test Survey",
                description = "Test Description",
                latitude = -6.2088,
                longitude = 106.8456,
                status = SurveyStatus.OPEN,
                createdAt = Instant.now()
            )
        )
        coEvery { getSurveysUseCase(any(), any()) } returns Result.success(surveys)
        
        // When
        val viewModel = DashboardViewModel(
            getSurveysUseCase = getSurveysUseCase,
            updateSurveyUseCase = updateSurveyUseCase,
            getSurveyStatsUseCase = getSurveyStatsUseCase
        )
        
        // Then
        assertEquals(surveys, viewModel.uiState.value.surveys)
        assertFalse(viewModel.uiState.value.isLoading)
    }
    
    @Test
    fun `should update search query and filter surveys`() = runTest {
        // Given
        val searchQuery = "Test"
        val filteredSurveys = listOf<Survey>()
        coEvery { 
            getSurveysUseCase(searchQuery, any()) 
        } returns Result.success(filteredSurveys)
        
        // When
        viewModel.onSearchQueryChanged(searchQuery)
        
        // Then
        assertEquals(searchQuery, viewModel.uiState.value.searchQuery)
        coVerify { getSurveysUseCase(searchQuery, null) }
    }
}
```

_Last updated: May 10, 2026_
