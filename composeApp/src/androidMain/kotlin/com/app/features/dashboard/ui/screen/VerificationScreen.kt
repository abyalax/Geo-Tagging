package com.app.features.dashboard.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.navigation.AuthMiddleware
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.model.SurveyStatus
import com.app.ui.components.BottomNavItem
import com.app.ui.components.BottomNavigationBar
import com.app.ui.components.TopAppBar

@Composable
fun VerificationScreen(
        onSuccess: (SurveyStatus) -> Unit,
        onCancel: () -> Unit,
        onShare: () -> Unit = {},
        onOpenMap: () -> Unit = {},
        locationName: String = "",
        username: String = "",
        onNavigateToHome: () -> Unit = {},
        onNavigateToLogin: () -> Unit = {},
        onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val actualUsername =
            if (username.isEmpty()) {
                // If no username provided, get from AuthMiddleware (must be authenticated)
                AuthMiddleware.getUsername(context)
                        ?: run {
                            // This should not happen if properly protected, but as safety net
                            AuthMiddleware.logout(context)
                            return
                        }
            } else {
                username
            }

    var isVerifying by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                    username = actualUsername,
                    onProfileClick = { /* Handle profile click */},
                    onNotificationClick = { /* Handle notification click */},
                    onStatsClick = { /* Handle stats click */}
            )

            // Main content with scroll
            Column(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                            text = "Verifikasi Survey",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (locationName.isNotEmpty()) {
                        Text(
                                text = locationName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Action Buttons Row (Maps & WA)
                    Row(
                            modifier = Modifier.padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FilledTonalIconButton(onClick = onOpenMap) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Open Maps")
                        }
                        FilledTonalIconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = "Share to WhatsApp")
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                        Text(
                                text =
                                        "Lakukan verifikasi untuk memastikan kondisi lapangan sesuai dengan laporan",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                        )
                    }

                    repeat(3) { index ->
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "✓", modifier = Modifier.padding(end = 12.dp))
                            Text(
                                    text =
                                            when (index) {
                                                0 -> "Cek lokasi geo-tagging"
                                                1 -> "Validasi dokumentasi foto"
                                                else -> "Verifikasi data survey"
                                            }
                            )
                        }
                    }

                    Column(
                            modifier = Modifier.padding(top = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                                onClick = {
                                    isVerifying = true
                                    onSuccess(SurveyStatus.VERIFIED)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isVerifying,
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                        )
                        ) { Text("Setujui (Verified)") }

                        Button(
                                onClick = {
                                    isVerifying = true
                                    onSuccess(SurveyStatus.REJECTED)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isVerifying,
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                        )
                        ) { Text("Tolak (Rejected)") }

                        OutlinedButton(
                                onClick = onCancel,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isVerifying
                        ) { Text("Batal") }
                    }
                }
            }

            // Bottom Navigation
            BottomNavigationBar(
                    selectedItem = BottomNavItem.Home,
                    onItemSelected = { item ->
                        when (item) {
                            BottomNavItem.Home -> onNavigateToHome()
                            BottomNavItem.Profile -> onNavigateToProfile()
                            BottomNavItem.Login -> onNavigateToLogin()
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview() {
    ApplicationTheme {
        VerificationScreen(onSuccess = {}, onCancel = {}, locationName = "Jalan Sudirman")
    }
}
