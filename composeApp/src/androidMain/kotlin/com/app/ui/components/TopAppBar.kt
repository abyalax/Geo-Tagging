package com.app.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TopAppBar(
        username: String = "",
        onProfileClick: () -> Unit = {},
        onNotificationClick: () -> Unit = {},
        onStatsClick: () -> Unit = {},
        onLogout: () -> Unit = {},
        navController: NavController? = null
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val actualUsername =
            if (username.isEmpty()) {
                prefs.getString("username", "") ?: ""
            } else {
                username
            }

    var showDropdown by remember { mutableStateOf(false) }

    Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar and username section
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar with dropdown
                Box(contentAlignment = Alignment.Center) {
                    Box(
                            modifier =
                                    Modifier.size(40.dp)
                                            .background(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = CircleShape
                                            )
                                            .clickable { showDropdown = true },
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                text = actualUsername.take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                        )
                    }

                    // Dropdown Menu
                    DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                    ) {
                        DropdownMenuItem(
                                text = { Text("Profile") },
                                onClick = {
                                    showDropdown = false
                                    onProfileClick()
                                }
                        )
                        DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    Log.d("TopAppBar", "Logout clicked")
                                    showDropdown = false
                                    onLogout()
                                    Log.d("TopAppBar", "onLogout() called")
                                }
                        )
                    }
                }

                // Username
                Text(
                        text = actualUsername,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                )
            }

            // Right side icons
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats icon
                IconButton(onClick = { onStatsClick() }) {
                    Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Stats",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Notification icon
                IconButton(onClick = { onNotificationClick() }) {
                    Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
