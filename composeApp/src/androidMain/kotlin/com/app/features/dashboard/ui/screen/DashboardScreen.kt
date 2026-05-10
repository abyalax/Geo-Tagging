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
    surveys: List<Survey> = emptyList(),
    isLoading: Boolean = false,
    onSurveyClick: (Survey) -> Unit = {},
    onShareSurvey: (Survey) -> Unit = {},
    onOpenSurveyMap: (Survey) -> Unit = {},
    searchQuery: String = "",
    onSearchChange: (String) -> Unit = {},
    selectedStatus: SurveyStatus? = null,
    onStatusFilterChange: (SurveyStatus?) -> Unit = {},
    username: String = "Field Officer",
    onNavigateToProfile: () -> Unit = {},
    onNavigateToVerification: (String, String) -> Unit = { _, _ -> },
    onNavigateToLogin: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    surveyStats: SurveyStats = SurveyStats()
) {
    // Debug logging
    android.util.Log.d("DashboardScreen", "DashboardScreen called! Username: $username")
    android.util.Log.d("DashboardScreen", "Surveys count: ${surveys.size}")
    android.util.Log.d("DashboardScreen", "Is loading: $isLoading")

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    placeholder = { Text("Search surveys...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true
                )

                // Filter chips
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter:", style = MaterialTheme.typography.labelSmall)

                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { onStatusFilterChange(null) },
                        label = { Text("All (${surveyStats.total})") }
                    )

                    FilterChip(
                        selected = selectedStatus == SurveyStatus.OPEN,
                        onClick = { onStatusFilterChange(SurveyStatus.OPEN) },
                        label = { Text("Open (${surveyStats.open})") }
                    )

                    FilterChip(
                        selected = selectedStatus == SurveyStatus.VERIFIED,
                        onClick = { onStatusFilterChange(SurveyStatus.VERIFIED) },
                        label = { Text("Verified (${surveyStats.verified})") }
                    )

                    FilterChip(
                        selected = selectedStatus == SurveyStatus.REJECTED,
                        onClick = { onStatusFilterChange(SurveyStatus.REJECTED) },
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
            }

            // Bottom Navigation
            BottomNavigationBar(
                selectedItem = BottomNavItem.Home,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.Login -> {
                            onNavigateToLogin()
                        }

                        BottomNavItem.Home -> {
                            // Already on home
                        }

                        BottomNavItem.Profile -> {
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
    ApplicationTheme {
        DashboardScreen(
            surveys = listOf(Survey(1, "Title", "Description", 0.0, 0.0, SurveyStatus.OPEN))
        )
    }
}
