---
name: Jetpack Compose Skills
description: How To Implement Jetpack Compose Stack
---

# Jetpack Compose Skills

## Technology Stack

### Core Libraries

- **Material3**: Design system with Material You
- **Navigation Compose**: Type-safe navigation
- **StateFlow**: Reactive state management
- **ViewModel**: Android lifecycle-aware state holder
- **Hilt DI**: Dependency injection (planned)

### Project-Specific Implementation

- **LazyColumn**: For 200+ survey items (RecyclerView replacement)
- **rememberSaveable**: State persistence across configuration changes
- **StateFlow + collectAsState()**: Reactive UI updates
- **Type-safe routing**: Centralized navigation management

## Architecture Patterns

### UiState Pattern

```kotlin
data class DashboardUiState(
    val surveys: List<Survey> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedStatus: SurveyStatus? = null
)
```

### Event-Based Actions

- User interactions trigger events
- Events processed by ViewModel
- State changes flow back to UI

### Stateless Composables

- Reusable components without internal state
- All state passed as parameters
- Easy to test and preview

## Current Implementation Examples

### DashboardScreen.kt

- LazyColumn with 200+ survey items
- Real-time search functionality
- Status filtering with chips
- Material3 design system

### LoginScreen.kt

- Form validation
- Session management integration
- Navigation on success

### VerificationScreen.kt

- Form data collection
- Status update workflow
- Map integration

## State Management Best Practices

### UI State Rules

1. **Local UI State**: `remember { mutableStateOf(...) }`
2. **Persistent State**: `rememberSaveable { ... }`
3. **Screen State**: ViewModel + StateFlow
4. **Business Logic**: Use Cases (Clean Architecture)

### Performance Optimization

- LazyColumn for large lists (memory efficient)
- Stable keys for item identification
- Minimal recomposition scope
- Derived state for expensive calculations

## Navigation Implementation

### Type-Safe Routes

```kotlin
sealed class Routes {
    object Login : Routes()
    object Dashboard : Routes()
    object Verification : Routes()
    object Profile : Routes()
}
```

### Centralized Navigation

- NavigationManager for navigation logic
- NavigationEvent for type-safe events
- Single-activity architecture

## Testing Strategies

### Compose Testing

- ComposeTestRule for UI tests
- Semantics for accessibility testing
- Preview functions for visual testing

### ViewModel Testing

- Unit tests for business logic
- StateFlow testing with Turbine
- Mock use cases for isolation

## Material3 Design System

### Components Used

- Scaffold: App structure
- TopAppBar: Header with actions
- FloatingActionButton: Primary actions
- Card: Survey list items
- TextField: Search input
- FilterChip: Status filters

### Theme Implementation

- Light/Dark theme support
- Material You color schemes
- Typography system
- Shape and elevation system

## Performance Considerations

### Memory Efficiency

- LazyColumn: Only visible items in memory (~20 of 200+)
- Automatic recycling
- O(1) lookup with stable keys

### CPU Optimization

- Minimal recomposition
- Derived state for expensive operations
- Background processing for heavy operations

_Last updated: May 10, 2026_
