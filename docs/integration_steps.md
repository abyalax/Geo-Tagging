# Integration Steps: Adding New Files to Your Project

## Overview
This guide shows exactly where to place the new files in your existing Android project structure.

---

## Project Structure Template

```
Project Root/
├── androidMain/
│   ├── app/
│   │   ├── core/
│   │   │   ├── common/
│   │   │   │   └── Constant.kt                  ✅ EXISTING
│   │   │   ├── navigation/
│   │   │   │   ├── ImplicitIntentHelper.kt      ✅ EXISTING
│   │   │   │   └── IntentManager.kt             ✅ EXISTING
│   │   │   ├── utils/
│   │   │   │   ├── MapUtils.kt                  ✅ EXISTING
│   │   │   │   └── ValidateCoordinate.kt        ✅ EXISTING
│   │   │   └── theme/
│   │   │       ├── Color.kt                     ✅ EXISTING
│   │   │       ├── Type.kt                      ✅ EXISTING
│   │   │       └── Theme.kt                     ✅ EXISTING
│   │   ├── features/
│   │   │   ├── auth/
│   │   │   │   └── login/
│   │   │   │       ├── activities/
│   │   │   │       │   └── LoginActivity.kt     ✅ EXISTING
│   │   │   │       └── ui/
│   │   │   │           ├── components/
│   │   │   │           │   └── LoginForm.kt     ✅ EXISTING
│   │   │   │           └── screen/
│   │   │   │               └── LoginScreen.kt   ✅ EXISTING
│   │   │   └── dashboard/
│   │   │       ├── activities/
│   │   │       │   ├── DashboardActivity.kt     ✅ EXISTING
│   │   │       │   └── VerificationActivity.kt  ✅ EXISTING
│   │   │       ├── data/
│   │   │       │   └── SurveyRepository.kt      ✨ NEW
│   │   │       ├── model/
│   │   │       │   ├── Sensor.kt                ✅ EXISTING
│   │   │       │   ├── Survey.kt                ✨ NEW
│   │   │       │   └── VerificationResult.kt    ✅ EXISTING
│   │   │       ├── viewmodel/
│   │   │       │   └── DashboardViewModel.kt    ✨ NEW
│   │   │       └── ui/
│   │   │           ├── components/
│   │   │           │   └── SurveyListItem.kt    ✨ NEW
│   │   │           └── screen/
│   │   │               ├── DashboardScreen.kt   ✅ EXISTING (replace with updated)
│   │   │               └── VerificationScreen.kt ✅ EXISTING
│   │   ├── MainActivity.kt                      ✅ EXISTING
│   │   └── Platform.android.kt                  ✅ EXISTING
│   └── build.gradle.kts
└── commonMain/
    └── app/
        └── core/
            └── theme/                           ✅ EXISTING

✨ = NEW (created in this project)
✅ = EXISTING (already in your project)
```

---

## Step-by-Step Integration

### Step 1: Create Directories (if not exists)

```bash
# If you don't already have these directories:
mkdir -p app/features/dashboard/data
mkdir -p app/features/dashboard/viewmodel
```

### Step 2: Copy NEW Files

#### File 1: Model
```
Copy: Survey.kt
To: app/features/dashboard/model/Survey.kt
Package: com.app.features.dashboard.model
```

#### File 2: Data Access Layer
```
Copy: SurveyRepository.kt
To: app/features/dashboard/data/SurveyRepository.kt
Package: com.app.features.dashboard.data
```

#### File 3: State Management
```
Copy: DashboardViewModel.kt
To: app/features/dashboard/viewmodel/DashboardViewModel.kt
Package: com.app.features.dashboard.viewmodel
```

#### File 4: UI Component
```
Copy: SurveyListItem.kt
To: app/features/dashboard/ui/components/SurveyListItem.kt
Package: com.app.features.dashboard.ui.components
```

#### File 5: Screen (Update Existing)
```
Copy: DashboardScreen_Updated.kt
To: app/features/dashboard/ui/screen/DashboardScreen.kt
Package: com.app.features.dashboard.ui.screen
(This replaces your existing DashboardScreen.kt)
```

### Step 3: Update build.gradle.kts

Add Compose dependencies if not already present:

```kotlin
dependencies {
    // Compose (should already be present)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    
    // ViewModel (should already be present)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Kotlin Coroutines (should already be present)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    // Optional: For ViewModel with Hilt
    // implementation(libs.androidx.hilt.navigation.compose)
    // implementation(libs.hilt.android)
    // kapt(libs.hilt.compiler)
}
```

### Step 4: Update DashboardActivity.kt

Replace your existing DashboardActivity with this integration:

```kotlin
package com.app.features.dashboard.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.core.common.Constants
import com.app.core.navigation.ImplicitIntentHelper
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.ui.screen.DashboardScreen
import com.app.features.dashboard.viewmodel.DashboardViewModel

class DashboardActivity : ComponentActivity() {
    
    // Initialize ViewModel
    private val viewModel: DashboardViewModel by viewModels()
    
    // ActivityResultAPI launcher for VerificationActivity
    private val verificationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val verificationStatus =
                    result.data?.getStringExtra(Constants.EXTRA_VERIFICATION_STATUS) ?: "UNKNOWN"
                // Status already updated in ViewModel, just refresh if needed
                viewModel.loadSurveys()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract sensor info from Intent
        val sensorName = intent.getStringExtra(Constants.EXTRA_SENSOR_NAME) 
            ?: Constants.DEFAULT_SENSOR_NAME
        val latitude = intent.getStringExtra(Constants.EXTRA_LATITUDE) 
            ?: Constants.DEFAULT_LATITUDE
        val longitude = intent.getStringExtra(Constants.EXTRA_LONGITUDE) 
            ?: Constants.DEFAULT_LONGITUDE

        setContent {
            // Observe ViewModel state
            val surveys by viewModel.filteredSurveys.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val errorMessage by viewModel.errorMessage.collectAsState()

            ApplicationTheme {
                DashboardScreen(
                    surveys = surveys,
                    isLoading = isLoading,
                    onSurveyClick = { survey ->
                        // Launch VerificationActivity
                        val intent = Intent(
                            this@DashboardActivity,
                            VerificationActivity::class.java
                        )
                        intent.putExtra(Constants.EXTRA_SENSOR_NAME, survey.title)
                        verificationLauncher.launch(intent)
                    },
                    onViewMap = {
                        ImplicitIntentHelper.openMaps(
                            context = this@DashboardActivity,
                            latitude = latitude,
                            longitude = longitude
                        )
                    },
                    onSearchChange = viewModel::searchSurveys,
                    onStatusFilterChange = viewModel::filterByStatus,
                    sensorName = sensorName,
                    latitude = latitude,
                    longitude = longitude
                )
                
                // Show error if exists
                if (errorMessage != null) {
                    // Show snackbar or dialog
                    viewModel.clearError()
                }
            }
        }
    }
}
```

### Step 5: Update Imports in Existing Files

#### In LoginActivity.kt:
```kotlin
// Make sure this import exists:
import com.app.core.navigation.IntentManager
```

#### In VerificationActivity.kt:
```kotlin
// Make sure these imports exist:
import com.app.core.common.Constants
import com.app.core.theme.ApplicationTheme
```

### Step 6: Sync & Build

```bash
# In Android Studio Terminal:
./gradlew clean build

# Or use Build menu:
Build → Clean Project
Build → Rebuild Project
```

---

## Verification Checklist

After integration, verify:

- [ ] Project builds without errors
- [ ] `Survey.kt` is recognized by IDE (no import errors)
- [ ] `SurveyRepository.kt` compiles (200 surveys loaded)
- [ ] `DashboardViewModel.kt` is instantiated in DashboardActivity
- [ ] `DashboardScreen.kt` shows LazyColumn of surveys
- [ ] Search bar filters surveys in real-time
- [ ] Filter chips update list when clicked
- [ ] Clicking a survey opens VerificationActivity
- [ ] Verifying/rejecting updates survey status
- [ ] Screen rotation preserves search query
- [ ] View Map button opens Google Maps

---

## Package Names

Ensure all package declarations match your project structure:

```kotlin
// Survey.kt
package com.app.features.dashboard.model

// SurveyRepository.kt
package com.app.features.dashboard.data

// DashboardViewModel.kt
package com.app.features.dashboard.viewmodel

// SurveyListItem.kt
package com.app.features.dashboard.ui.components

// DashboardScreen.kt
package com.app.features.dashboard.ui.screen
```

---

## Dependencies

### Already Included (Check your build.gradle.kts):

```kotlin
// Compose Core
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.compose.runtime:runtime

// Lifecycle & ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose
androidx.lifecycle:lifecycle-runtime-compose

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-core
org.jetbrains.kotlinx:kotlinx-coroutines-android
```

### Optional (For Advanced Features):

```kotlin
// Hilt Dependency Injection
com.google.dagger:hilt-android
com.google.dagger:hilt-compiler

// Navigation (if upgrading to Compose Navigation)
androidx.navigation:navigation-compose

// Room Database (for persistence)
androidx.room:room-runtime
androidx.room:room-compiler
```

---

## Common Issues & Solutions

### Issue 1: Import Errors
**Problem:** `Cannot find symbol: class DashboardViewModel`
**Solution:** Make sure DashboardViewModel.kt is in the correct package path

### Issue 2: LazyColumn Not Working
**Problem:** `Can't find LazyColumn`
**Solution:** Ensure compose.foundation dependency is in build.gradle.kts

### Issue 3: StateFlow Not Available
**Problem:** `Cannot find symbol: StateFlow`
**Solution:** Add kotlinx-coroutines-core dependency

### Issue 4: ViewModel Not Injected
**Problem:** `Cannot create instance of class DashboardViewModel`
**Solution:** Either use `by viewModels()` or create manually: `DashboardViewModel()`

### Issue 5: Preview Not Working
**Problem:** Preview won't render
**Solution:** Ensure `@Preview` annotation and ApplicationTheme are correct

---

## Testing the Integration

### Test 1: Launch Activity
```bash
adb shell am start -n com.app/.features.dashboard.activities.LoginActivity
```

### Test 2: Check Logs
```bash
adb logcat | grep "SurveyVM\|DashboardViewModel"
```

### Test 3: UI Espresso Test
```kotlin
@Test
fun testSurveyListDisplayed() {
    composeTestRule.setContent {
        DashboardScreen(surveys = mockSurveys)
    }
    composeTestRule.onNodeWithText("Jalan Sudirman").assertExists()
}
```

---

## Performance Validation

### Check Memory Usage
- Before: Open list, scroll → watch RAM in Android Profiler
- After: Should be ~5-10MB for 200 items (LazyColumn efficiency)

### Check Frame Rate
- Before: ~30-40fps when scrolling
- After: Should be ~55-60fps (smooth scrolling)

---

## Next Steps After Integration

1. **Test thoroughly** - Run on multiple devices
2. **Customize colors** - Modify Material 3 theme in Theme.kt
3. **Replace static data** - Connect to real API/database
4. **Add error handling** - Network timeouts, empty states
5. **Implement caching** - Room database for offline support
6. **Add monitoring** - Firebase Crashlytics, Analytics
7. **Performance tuning** - Profile with Android Profiler

---

## Support Resources

- **QUICK_REFERENCE.md** - Common modifications & debugging
- **COMPOSE_MAPPING.md** - Understanding traditional → Compose equivalents
- **IMPLEMENTATION_GUIDE.md** - Deep dive into architecture

---

## Success Criteria

✅ Project builds without errors
✅ App launches and shows login screen
✅ Can navigate to dashboard with 200+ surveys
✅ Search and filter work correctly
✅ Verification updates survey status
✅ Screen rotation preserves state
✅ No memory leaks in Profiler
✅ No janky scrolling (60fps)

**When all criteria are met, integration is complete!**