# 1. Context (Business / Use Case)

## Title

**Field Survey & Geo-Tagging Application**

## Background

This application is designed for **field officers** to record and verify real-world conditions at specific locations, such as:

* Damaged roads
* Flooded areas
* Broken street lights
* Infrastructure issues

The collected data is:

* location-based (latitude, longitude)
* verifiable by other officers
* shareable with external stakeholders

---

## Actors

### Field Officer (User)

* Logs into the application
* Views survey reports
* Verifies or rejects reports
* Shares report information

---

## Core Problem

* Lack of a simple system for recording field conditions
* Poor coordination between field officers
* Unstructured and inconsistent reporting

---

## Proposed Solution

A mobile application that:

* displays a list of survey reports
* allows verification of reports
* integrates with external apps (maps, sharing)

---

# 2. Functional Requirements

---

## FR-1: Authentication

Users must be able to log into the application.

**Implementation:**

* `LoginActivity`
* `LoginScreen`

---

## FR-2: Display Survey List (Dynamic Data)

The application must display a list of at least **20 survey items**.

**Implementation:**

* `DashboardScreen`
* `LazyColumn` (Compose equivalent of RecyclerView)

---

## FR-3: Detail View & Navigation (Explicit Intent)

Users can select a survey item to view its details.

**Implementation:**

* Navigation from:

```plaintext
DashboardActivity → VerificationActivity
```

* Data is passed using:

```kotlin
Intent.putExtra(...)
```

---

## FR-4: Verification Process

Users can:

* ✔ Verify a report
* ❌ Reject a report

The report status changes:

* `OPEN → VERIFIED / REJECTED`

**Implementation:**

* `VerificationScreen`
* Use `setResult()` to return updated data

---

## FR-5: Implicit Intent Integration

### a. Open Map

Open the survey location in a map application:

```plaintext
geo:latitude,longitude?q=latitude,longitude
```

---

### b. Share Report

Share report details via other apps:

```kotlin
Intent.ACTION_SEND
```

---

## FR-6: Lifecycle & State Management

User input must be preserved during configuration changes (e.g., screen rotation).

**Implementation:**

* `ViewModel` or `rememberSaveable`

---

## FR-7: Multi-Screen Architecture

The application must contain at least two screens:

* Dashboard
* Verification

**Implementation:**

* Activity-based navigation

---

# 3. Non-Functional Requirements

* Responsive user interface
* State persistence during configuration changes
* Clear navigation flow
* Modular architecture (feature-based structure)

---

# 4. Data Model

## Survey

```kotlin
data class Survey(
    val id: Int,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val status: SurveyStatus
)
```

---

## SurveyStatus

```kotlin
enum class SurveyStatus {
    OPEN,
    VERIFIED,
    REJECTED
}
```

---

# 5. User Flow Summary

```plaintext
Login
  ↓
Dashboard (survey list)
  ↓ (select item)
Verification Screen
  ↓
[Verify / Reject]
  ↓
Return result → update list
```

---
