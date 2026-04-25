package com.app.features.dashboard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.model.SurveyStatus

@Composable
fun VerificationScreen(
    onSuccess: (SurveyStatus) -> Unit, 
    onCancel: () -> Unit,
    onShare: () -> Unit = {},
    onOpenMap: () -> Unit = {},
    sensorName: String = ""
) {
  var isVerifying by remember { mutableStateOf(false) }

  Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
  ) {
    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
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
        
        if (sensorName.isNotEmpty()) {
            Text(
                text = sensorName,
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
                  text = "Lakukan verifikasi untuk memastikan kondisi lapangan sesuai dengan laporan",
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
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) { Text("Setujui (Verified)") }
          
          Button(
              onClick = {
                isVerifying = true
                onSuccess(SurveyStatus.REJECTED)
              },
              modifier = Modifier.fillMaxWidth(),
              enabled = !isVerifying,
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
          ) { Text("Tolak (Rejected)") }

          OutlinedButton(
                  onClick = onCancel,
                  modifier = Modifier.fillMaxWidth(),
                  enabled = !isVerifying
          ) { Text("Batal") }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview() {
    ApplicationTheme {
        VerificationScreen(onSuccess = {}, onCancel = {}, sensorName = "Jalan Sudirman")
    }
}
