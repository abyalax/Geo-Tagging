package com.app.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Login : BottomNavItem("login", Icons.Default.ExitToApp, "Login")
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@Composable
fun BottomNavigationBar(
        items: List<BottomNavItem> =
                listOf(BottomNavItem.Login, BottomNavItem.Home, BottomNavItem.Profile),
        selectedItem: BottomNavItem = BottomNavItem.Home,
        onItemSelected: (BottomNavItem) -> Unit = {},
        modifier: Modifier = Modifier
) {
    Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
    ) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(80.dp)
                                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    BottomNavItem(
                            item = item,
                            isSelected = item == selectedItem,
                            onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier =
                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable {
                        Log.d(
                                "BottomNavigationBar",
                                "BottomNavItem clicked: ${item.label} (${item.route})"
                        )
                        onClick()
                    }
    ) {
        Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
