package com.app.features.dashboard.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.model.Survey
import com.app.features.dashboard.model.SurveyStats
import com.app.features.dashboard.model.SurveyStatus
import com.app.features.dashboard.ui.components.SurveyListItem
import com.app.ui.components.BottomNavItem
import com.app.ui.components.BottomNavigationBar
import com.app.ui.components.TopAppBar

/** DashboardScreen - Main survey list display with search and filter */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
        isLoading: Boolean = false,
        surveys: List<Survey> = emptyList(),
        onSurveyClick: (Survey) -> Unit = {},
        onShareSurvey: (Survey) -> Unit = {},
        onOpenSurveyMap: (Survey) -> Unit = {},
        searchQuery: String = "",
        onSearchChange: (String) -> Unit = {},
        selectedStatus: SurveyStatus? = null,
        onStatusFilterChange: (SurveyStatus?) -> Unit = {},
        username: String = "",
        onNavigateToProfile: () -> Unit = {},
        onNavigateToVerification: (String, String) -> Unit = { _, _ -> },
        onNavigateToLogin: () -> Unit = {},
        onNotificationClick: () -> Unit = {},
        onStatsClick: () -> Unit = {},
        onLogout: () -> Unit = {},
        surveyStats: SurveyStats = SurveyStats(),
        modifier: Modifier = Modifier
) {
    var savedSearchQuery by rememberSaveable { mutableStateOf(searchQuery) }
    var savedSelectedStatusName by rememberSaveable { mutableStateOf(selectedStatus?.name) }
    val visibleSurveys =
            surveys.filter { survey ->
                val matchesSearch =
                        savedSearchQuery.isBlank() ||
                                survey.title.contains(savedSearchQuery, ignoreCase = true) ||
                                survey.description.contains(savedSearchQuery, ignoreCase = true)
                val matchesStatus =
                        savedSelectedStatusName == null || survey.status.name == savedSelectedStatusName
                matchesSearch && matchesStatus
            }

    android.util.Log.d("DashboardScreen", "DashboardScreen called! Username: $username")
    android.util.Log.d("DashboardScreen", "Surveys count: ${surveys.size}")
    android.util.Log.d("DashboardScreen", "Is loading: $isLoading")

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                    username = username,
                    onProfileClick = onNavigateToProfile,
                    onNotificationClick = onNotificationClick,
                    onStatsClick = onStatsClick,
                    onLogout = onLogout
            )

            // Main content with weight
            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Search bar
                OutlinedTextField(
                        value = savedSearchQuery,
                        onValueChange = {
                            savedSearchQuery = it
                            onSearchChange(it)
                        },
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        placeholder = { Text("Search surveys...") },
                        leadingIcon = {
                            Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (savedSearchQuery.isNotEmpty()) {
                                IconButton(
                                        onClick = {
                                            savedSearchQuery = ""
                                            onSearchChange("")
                                        }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true
                )

                // Filter chips
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                        .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter:", style = MaterialTheme.typography.labelSmall)

                    FilterChip(
                            selected = savedSelectedStatusName == null,
                            onClick = {
                                savedSelectedStatusName = null
                                onStatusFilterChange(null)
                            },
                            label = { Text("All (${surveyStats.total})") }
                    )

                    FilterChip(
                            selected = savedSelectedStatusName == SurveyStatus.OPEN.name,
                            onClick = {
                                savedSelectedStatusName = SurveyStatus.OPEN.name
                                onStatusFilterChange(SurveyStatus.OPEN)
                            },
                            label = { Text("Open (${surveyStats.open})") }
                    )

                    FilterChip(
                            selected = savedSelectedStatusName == SurveyStatus.VERIFIED.name,
                            onClick = {
                                savedSelectedStatusName = SurveyStatus.VERIFIED.name
                                onStatusFilterChange(SurveyStatus.VERIFIED)
                            },
                            label = { Text("Verified (${surveyStats.verified})") }
                    )

                    FilterChip(
                            selected = savedSelectedStatusName == SurveyStatus.REJECTED.name,
                            onClick = {
                                savedSelectedStatusName = SurveyStatus.REJECTED.name
                                onStatusFilterChange(SurveyStatus.REJECTED)
                            },
                            label = { Text("Rejected (${surveyStats.rejected})") }
                    )
                }

                // Survey list
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(visibleSurveys, key = { it.id }) { survey ->
                            SurveyListItem(
                                    survey = survey,
                                    onClick = { onSurveyClick(it) },
                                    onShare = { onShareSurvey(it) },
                                    onOpenMap = { onOpenSurveyMap(it) }
                            )
                        }
                    }
                }
            }

            // Bottom Navigation
            BottomNavigationBar(
                    selectedItem = BottomNavItem.Home,
                    onItemSelected = { item ->
                        android.util.Log.d(
                                "DashboardScreen",
                                "BottomNavigationBar item selected: ${item.label}"
                        )
                        when (item) {
                            BottomNavItem.Login -> {
                                android.util.Log.d(
                                        "DashboardScreen",
                                        "Login selected - calling onNavigateToLogin"
                                )
                                onNavigateToLogin()
                            }
                            BottomNavItem.Home -> {
                                android.util.Log.d(
                                        "DashboardScreen",
                                        "Home selected - already on home"
                                )
                                // Already on home
                            }
                            BottomNavItem.Profile -> {
                                android.util.Log.d(
                                        "DashboardScreen",
                                        "Profile selected - calling onNavigateToProfile"
                                )
                                onNavigateToProfile()
                            }
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    ApplicationTheme { DashboardScreen() }
}
