package com.app.features.dashboard.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.model.Survey
import com.app.features.dashboard.model.SurveyStatus

@Composable
fun SurveyListItem(
    survey: Survey,
    modifier: Modifier = Modifier,
    onClick: (Survey) -> Unit = {},
    onShare: (Survey) -> Unit = {},
    onOpenMap: (Survey) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(survey) }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Title & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = survey.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                StatusBadge(status = survey.status)
            }

            // Description
            Text(
                text = survey.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)
            )

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = Icons.Default.LocationOn,
                    label = "Maps",
                    onClick = { onOpenMap(survey) }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                ActionButton(
                    icon = Icons.Default.Share,
                    label = "WA",
                    onClick = { onShare(survey) }
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun StatusBadge(
    status: SurveyStatus,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        SurveyStatus.OPEN -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
        SurveyStatus.VERIFIED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        SurveyStatus.REJECTED -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
    }

    val textColor = when (status) {
        SurveyStatus.OPEN -> MaterialTheme.colorScheme.tertiary
        SurveyStatus.VERIFIED -> MaterialTheme.colorScheme.primary
        SurveyStatus.REJECTED -> MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = backgroundColor
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SurveyListItemPreview() {
    ApplicationTheme {
        SurveyListItem(
            survey = Survey(
                id = 1,
                title = "Jalan Sudirman - Lubang Aspal Besar",
                description = "Lubang besar di jalan utama, diameter ~50cm",
                latitude = -6.2088,
                longitude = 106.8000,
                status = SurveyStatus.OPEN
            )
        )
    }
}
