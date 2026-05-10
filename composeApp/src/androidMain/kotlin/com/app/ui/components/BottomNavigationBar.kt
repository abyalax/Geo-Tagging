package com.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Login : BottomNavItem("login", Icons.Default.ArrowBack, "Login")
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
    // Animate scale when selected
    val scale by
            animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = tween(durationMillis = 200),
                    label = "scale"
            )

    // Animate size
    val animatedSize by
            animateFloatAsState(
                    targetValue = if (isSelected) 56f else 48f,
                    animationSpec = tween(durationMillis = 200),
                    label = "size"
            )

    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clickable { onClick() }
    ) {
        Box(
                contentAlignment = Alignment.Center,
                modifier =
                        Modifier.scale(scale)
                                .size(animatedSize.dp)
                                .background(
                                        color =
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else Color.Transparent,
                                        shape = CircleShape
                                )
        ) {
            Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint =
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
            )
        }

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
