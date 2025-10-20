package ie.setu.carmaintenenceapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier,viewModel: CarViewModel) {
    val carName = viewModel.carName.value
    val nextService = viewModel.reminders.lastOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your Current Car:", style = MaterialTheme.typography.titleLarge)
        Text(carName, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(24.dp))
        Text("Next Service Due:", style = MaterialTheme.typography.titleMedium)
        if (nextService != null) {
            Card(
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(nextService.title, style = MaterialTheme.typography.titleMedium)
                    Text("Date: ${nextService.date}")
                    Text(nextService.description)
                }
            }
        } else {
            Text("No upcoming services added yet.")
        }
    }
}