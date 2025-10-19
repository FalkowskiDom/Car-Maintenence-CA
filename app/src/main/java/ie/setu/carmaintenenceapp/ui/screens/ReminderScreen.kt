package ie.setu.carmaintenenceapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReminderScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Service Reminders", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text("• 12 Nov 2025 - Oil Change")
        Text("• 22 Jan 2026 - Brake Fluid Check")
    }
}
