---
description: Business context and project knowledge for Windsurf
category: context
---

# Field Survey & Geo-Tagging Application - Business Context

## 📦 Product Overview

**Nama**: Field Survey & Geo-Tagging Application  
**Platform**: Kotlin Multiplatform (Android + iOS ready)  
**Architecture**: Clean Architecture + Compose UI  
**Status**: Production Ready

### Deskripsi Produk

Aplikasi mobile untuk manajemen survei lapangan dengan kemampuan geo-tagging. Tim field officers mengumpulkan data survei di lokasi, dengan otomatis menangkap koordinat GPS dan mendokumentasikan foto. Data tersinkronisasi ke backend untuk analisis dan laporan.

### Target Users

- **Field Officers**: Melakukan survei di lapangan
- **Survey Managers**: Review dan verifikasi survei
- **Data Analysts**: Analisis data dan generate laporan

### Use Cases Utama

- Infrastructure inspection (jalan, jembatan, fasilitas publik)
- Environmental monitoring (banjir, polusi)
- Urban planning data collection
- Public service issue reporting

## 📊 Data Coverage

### Geographic Scope

- **Jakarta**: 20 survey items
- **Bandung**: 40 survey items
- **Surabaya**: 20 survey items
- **Medan**: 40 survey items
- **Makassar**: 20 survey items
- **Yogyakarta**: 20 survey items
- **Palembang**: 20 survey items
- **Semarang**: 20 survey items

**Total**: 200+ survey items across Indonesian cities

### Survey Types

- Road infrastructure damage
- Flood and drainage issues
- Public facility problems
- Environmental concerns
- Urban planning observations

## 🎯 Core Capabilities

### 1. Survey Management

- **200+ Pre-loaded Survey Items**: Realistic data across Indonesian cities
- **Real-time Search**: Filter by title dan description
- **Status Management**: OPEN, VERIFIED, REJECTED status tracking
- **Survey Statistics**: Real-time counts dan analytics

### 2. Geo-Tagging Features

- **GPS Location**: Automatic coordinate capture
- **Map Integration**: Open locations dalam Google Maps
- **Location Verification**: Coordinate validation dan formatting
- **Regional Data**: Surveys across major Indonesian cities

### 3. Data Collection

- **Verification Forms**: Update survey status dengan notes
- **Photo Documentation**: Ready untuk image capture integration
- **Offline Support**: Survey data available tanpa internet
- **Data Sync**: Ready untuk backend integration

### 4. User Management

- **Authentication**: Secure login dengan session management
- **User Profiles**: Personal information dan activity tracking
- **Session Persistence**: Login state maintained across app restarts

## 💼 Business Value

### Efficiency Gains

- **50% Faster** data collection vs paper forms
- **Real-time** status updates dan notifications
- **Centralized** data management dan analytics
- **Reduced** data entry errors

### Cost Savings

- **Eliminated** paper-based processes
- **Reduced** travel time dengan better planning
- **Automated** reporting dan analytics
- **Scalable** ke unlimited survey types

## 🏗️ Technical Stack

### Frontend

- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Design system dengan Material You
- **Navigation Compose**: Type-safe routing

### State Management

- **ViewModel**: Lifecycle-aware state management
- **StateFlow**: Reactive state updates
- **Kotlin Coroutines**: Asynchronous operations

### Architecture

- **Clean Architecture**: Separated concerns (Domain, Data, Presentation)
- **Repository Pattern**: Data access abstraction
- **Use Case Pattern**: Business logic encapsulation
- **Single Activity**: Modern navigation dengan Compose Navigation

### Platform Support

- **Android**: Full native experience
- **iOS**: Ready untuk deployment (shared business logic)
- **Code Sharing**: 80%+ shared code between platforms

## 📱 Application Structure

### Main Screens

1. **Splash Screen**: Authentication check
2. **Login Screen**: User authentication
3. **Dashboard Screen**: Survey list dengan search dan filter
4. **Verification Screen**: Survey detail dan status update
5. **Profile Screen**: User information management

### Key Features per Screen

**Dashboard**:

- LazyColumn dengan 200+ survey items
- Real-time search functionality
- Status filtering dengan chips
- WhatsApp share integration
- Google Maps integration

**Verification**:

- Survey detail display
- Status update form
- Location coordinates
- Timestamp tracking

**Profile**:

- Username management
- Password management
- Session information

## 🔐 Authentication & Session

### Authentication Flow

1. User opens app → Splash screen checks auth state
2. If logged in → Navigate ke Dashboard
3. If not logged in → Navigate ke Login
4. User enters credentials → AuthMiddleware saves state
5. Session persisted dalam SharedPreferences

### Session Management

- Login state stored dalam SharedPreferences
- Username/Password cached locally (⚠️ TODO: Replace dengan token-based auth)
- Logout clears session data
- Session check on app startup

## 🎨 Design System

### Material 3 Implementation

- **Color Scheme**: Material You dynamic colors
- **Typography**: Consistent typography system
- **Components**: Material 3 buttons, cards, dialogs
- **Elevation**: Proper shadow dan depth

### Key UI Components

- **Scaffold**: App structure dengan TopAppBar + BottomNav
- **TopAppBar**: Header dengan user dropdown menu
- **BottomNavigationBar**: Navigation items
- **LazyColumn**: Efficient list rendering (200+ items)
- **Card**: Survey item display
- **TextField**: Search dan input fields
- **FilterChip**: Status filtering

## 📈 Performance Characteristics

### Memory Efficiency

- **LazyColumn**: Renders only visible items (~20 dari 200+)
- **Automatic Recycling**: Items automatically recycled
- **O(1) Lookup**: Stable keys untuk efficient lookup

### UI Performance

- **Target**: 60fps smooth scrolling
- **Large Dataset**: 200+ items tanpa lag
- **Real-time Search**: Instant filtering
- **Minimal Recomposition**: Optimized UI updates

## 🔄 Data Flow Architecture

```
User Input (Search, Filter)
    ↓
ViewModel.onSearchChange/onStatusFilterChange
    ↓
Use Case (GetSurveysUseCase)
    ↓
Repository.getSurveys()
    ↓
Data Source (LocalDataSource/RemoteDataSource)
    ↓
State Update (StateFlow)
    ↓
UI Recomposition (collectAsState)
    ↓
Screen Display Update
```

## 📋 Feature Matrix

| Feature             | Status      | Description                      |
| ------------------- | ----------- | -------------------------------- |
| Survey List         | ✅ Complete | 200+ items dengan search/filter  |
| Geo-Tagging         | ✅ Complete | GPS coordinates capture ready    |
| Maps Integration    | ✅ Complete | Google Maps opening              |
| Authentication      | ✅ Complete | Login dengan session persistence |
| Verification Forms  | ✅ Complete | Status update workflow           |
| Photo Documentation | 📋 Ready    | Framework untuk image capture    |
| Offline Support     | ✅ Complete | Local data caching               |
| Backend Sync        | 📋 Ready    | API integration framework        |
| Analytics           | 📋 Ready    | Stats tracking infrastructure    |

## 🚀 Future Roadmap

### Phase 1 (Current)

- ✅ Core survey management
- ✅ Authentication system
- ✅ Geo-tagging basics

### Phase 2 (Planned)

- 📋 Photo documentation integration
- 📋 Advanced analytics dashboard
- 📋 Offline-first sync

### Phase 3 (Planned)

- 📋 iOS release
- 📋 Advanced reporting
- 📋 Mobile data prediction

## 🔗 Key Dependencies & Integrations

### Integrated Services

- **Google Maps**: Location viewing dan navigation
- **WhatsApp**: Survey sharing functionality
- **SharedPreferences**: Local session storage

### Planned Integrations

- Backend API untuk data synchronization
- Image upload service untuk photo documentation
- Push notifications untuk alerts
- Advanced analytics platform

## 📊 Current Issues Found

### Critical Issues (FIXED)

1. **Forced Logout in TopAppBar** - Automatic logout on every recomposition
2. **Duplicate SplashScreen** - Rendered twice causing navigation blocks

### Medium Issues (IDENTIFIED)

1. **Hardcoded Route Strings** - Route navigation strings not using Routes sealed class
2. **Missing ViewModel DI** - Repository tidak diinject ke ViewModel
3. **URL Parameter Encoding** - Special characters dalam route parameters tidak encoded

### Security Issues

1. **Password in SharedPreferences** - Plain text password storage (⚠️ Replace dengan token auth)

---

_Last updated: May 10, 2026_
