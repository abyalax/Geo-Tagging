package com.app.features.dashboard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.model.Survey
import com.app.features.dashboard.model.SurveyStatus
import com.app.features.dashboard.ui.components.SurveyListItem

/**
 * DashboardScreen - Main survey list display with search and filter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    surveys: List<Survey> = emptyList(),
    isLoading: Boolean = false,
    onSurveyClick: (Survey) -> Unit = {},
    onViewMap: () -> Unit = {},
    onShareSurvey: (Survey) -> Unit = {},
    onOpenSurveyMap: (Survey) -> Unit = {},
    searchQuery: String = "",
    onSearchChange: (String) -> Unit = {},
    selectedStatus: SurveyStatus? = null,
    onStatusFilterChange: (SurveyStatus?) -> Unit = {},
    sensorName: String = "Field Officer",
    latitude: String = "",
    longitude: String = "",
    verificationStatus: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with officer info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Field Survey Dashboard",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Officer: $sensorName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    IconButton(onClick = onViewMap) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "My Location",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
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

                FilterChip(
                    selected = selectedStatus == SurveyStatus.VERIFIED,
                    onClick = { onStatusFilterChange(SurveyStatus.VERIFIED) },
                    label = { Text("Verified") }
                )

                FilterChip(
                    selected = selectedStatus == SurveyStatus.REJECTED,
                    onClick = { onStatusFilterChange(SurveyStatus.REJECTED) },
                    label = { Text("Rejected") }
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
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    ApplicationTheme {
        DashboardScreen(
            surveys = listOf(
                Survey(1, "Title", "Description", 0.0, 0.0, SurveyStatus.OPEN)
            )
        )
    }
}
