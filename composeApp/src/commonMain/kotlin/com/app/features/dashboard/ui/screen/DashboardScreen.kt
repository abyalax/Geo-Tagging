package com.app.features.dashboard.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.features.dashboard.domain.model.DashboardUiState
import com.app.features.dashboard.domain.model.Survey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
        uiState: DashboardUiState,
        onSurveyClick: (Survey) -> Unit,
        onShareSurvey: (Survey) -> Unit,
        onOpenSurveyMap: (Survey) -> Unit,
        onSearchChange: (String) -> Unit,
        onStatusFilterChange: (com.app.features.dashboard.domain.model.SurveyStatus?) -> Unit,
        onNavigateToProfile: () -> Unit,
        onNavigateToVerification: (String, String) -> Unit,
        onNavigateToLogin: () -> Unit,
        onLogout: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "Welcome, ${uiState.username}",
                    style = MaterialTheme.typography.headlineSmall
            )

            Row {
                Button(onClick = onNavigateToProfile) { Text("Profile") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onLogout) { Text("Logout") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search and Filter
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    label = { Text("Search surveys...") },
                    modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterButton(
                    selectedStatus = uiState.selectedStatus,
                    onStatusFilterChange = onStatusFilterChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.filteredSurveys.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No surveys found")
                }
            }
            else -> {
                LazyColumn {
                    items(uiState.filteredSurveys) { survey ->
                        SurveyItem(
                                survey = survey,
                                onSurveyClick = onSurveyClick,
                                onShareSurvey = onShareSurvey,
                                onOpenSurveyMap = onOpenSurveyMap
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterButton(
        selectedStatus: com.app.features.dashboard.domain.model.SurveyStatus?,
        onStatusFilterChange: (com.app.features.dashboard.domain.model.SurveyStatus?) -> Unit
) {
    val statuses =
            listOf(
                    null,
                    com.app.features.dashboard.domain.model.SurveyStatus.OPEN,
                    com.app.features.dashboard.domain.model.SurveyStatus.VERIFIED,
                    com.app.features.dashboard.domain.model.SurveyStatus.REJECTED
            )

    val statusLabels =
            mapOf(
                    null to "All",
                    com.app.features.dashboard.domain.model.SurveyStatus.OPEN to "Open",
                    com.app.features.dashboard.domain.model.SurveyStatus.VERIFIED to "Verified",
                    com.app.features.dashboard.domain.model.SurveyStatus.REJECTED to "Rejected"
            )

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        statuses.forEach { status ->
            FilterChip(
                    selected = selectedStatus == status,
                    onClick = { onStatusFilterChange(status) },
                    label = { Text(statusLabels[status] ?: "All") }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SurveyItem(
        survey: Survey,
        onSurveyClick: (Survey) -> Unit,
        onShareSurvey: (Survey) -> Unit,
        onOpenSurveyMap: (Survey) -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            onClick = { onSurveyClick(survey) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = survey.title, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                    text = survey.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = survey.status.name,
                        style = MaterialTheme.typography.bodySmall,
                        color =
                                when (survey.status) {
                                    com.app.features.dashboard.domain.model.SurveyStatus.OPEN ->
                                            MaterialTheme.colorScheme.primary
                                    com.app.features.dashboard.domain.model.SurveyStatus.VERIFIED ->
                                            MaterialTheme.colorScheme.tertiary
                                    com.app.features.dashboard.domain.model.SurveyStatus.REJECTED ->
                                            MaterialTheme.colorScheme.error
                                }
                )

                Row {
                    IconButton(onClick = { onShareSurvey(survey) }) { Text("Share") }
                    IconButton(onClick = { onOpenSurveyMap(survey) }) { Text("Map") }
                }
            }
        }
    }
}
