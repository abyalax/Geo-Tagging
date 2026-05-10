package com.app.features.verification.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.features.verification.presentation.viewmodel.VerificationUiState

@Composable
fun VerificationScreen(
        surveyId: String,
        locationName: String,
        uiState: VerificationUiState,
        onSuccess: () -> Unit,
        onCancel: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Verification", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Survey ID: $surveyId")
        Text("Location: $locationName")

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.error != null) {
            Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                    onClick = onSuccess,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.weight(1f)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Verify")
                }
            }

            Button(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
        }
    }
}
