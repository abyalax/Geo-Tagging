---
name: Clean Architecture
description: How To Implement Clean Architecture
---

# Clean Architecture

## Core Layers

### 1. Domain Layer (commonMain/core/domain/)

- **Purpose:** Business logic and rules, independent of frameworks
- **Components:**
  - Use Cases: Business operations (LoginUseCase, GetSurveysUseCase, etc.)
  - Repository Interfaces: Data access contracts (AuthRepository, SurveyRepository)
  - Domain Models: Core business entities (UserSession, Survey, UiState)
- **Rules:** No framework dependencies, pure Kotlin

### 2. Data Layer (androidMain/core/data/)

- **Purpose:** Data implementation and external integrations
- **Components:**
  - Repository Implementations: Concrete data access (SurveyRepository.kt)
  - Data Sources: API, database, file system
  - Mappers: Convert between domain and external models
- **Rules:** Depends on domain, implements interfaces

### 3. Presentation Layer (androidMain/features/\*/presentation/)

- **Purpose:** UI state management and user interactions
- **Components:**
  - ViewModels: State management (DashboardViewModel, AuthViewModel)
  - UI Components: Compose screens and composables
  - Navigation: Screen routing and navigation logic
- **Rules:** Depends on domain through use cases

## Architecture Rules

### Dependency Rule

- Dependencies point inward: Presentation → Domain ← Data
- Domain layer has NO dependencies on other layers
- Outer layers can depend on inner layers

### Implementation Rules

- **Repository Pattern:** Interface in domain, implementation in data
- **Use Case Pattern:** One use case per business operation
- **Mapper Separation:** Mandatory separation between domain and external models
- **State Management:** ViewModel + StateFlow for presentation state

### Data Flow

```
UI Event → ViewModel → Use Case → Repository → Data Source
          ↓
      UI Update ← StateFlow ← Result ← Repository
```

## Project-Specific Implementation

### Current Use Cases

- `LoginUseCase`: Authentication logic
- `GetSessionUseCase`: Session management
- `LogoutUseCase`: Session cleanup
- `GetSurveysUseCase`: Survey data retrieval

### Current Repositories

- `AuthRepository`: Authentication data access
- `SurveyRepository`: 200+ survey items management

### Navigation Architecture

- Type-safe routing with `Routes.kt`
- Centralized navigation with `NavigationManager.kt`
- Event-based navigation with `NavigationEvent.kt`

## Benefits in This Project

1. **Testability:** Each layer can be unit tested independently
2. **Maintainability:** Clear separation of concerns
3. **Scalability:** Easy to add new features without affecting existing code
4. **KMP Compatibility:** Shared business logic across platforms

_Last updated: May 10, 2026_
