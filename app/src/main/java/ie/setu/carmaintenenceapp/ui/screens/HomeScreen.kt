package ie.setu.carmaintenenceapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: CarViewModel) {
    // Read current saved car details from ViewModel state
    val carMake = viewModel.carMake.value
    val carModel = viewModel.carModel.value
    val carReg = viewModel.carReg.value
    val carMileage = viewModel.carMileage.intValue
    val serviceInterval = viewModel.serviceInterval.intValue
    val lastService = viewModel.lastServiceDate.value

    // Basic calculation for next maintenance recommendation
    val nextServiceMileage = carMileage + serviceInterval

    // List of saved service reminders
    val reminders = viewModel.reminders

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card showing current car profile info
        Card(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Your Car",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Make & Model: $carMake $carModel",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(8.dp))

                Text("Last Service: $lastService", style = MaterialTheme.typography.bodyMedium)
                Text("Next Service Due:", style = MaterialTheme.typography.titleMedium)

                Text(
                    "Next Service Mileage: $nextServiceMileage km",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Section heading for upcoming reminders list
        Text("Upcoming Reminders", style = MaterialTheme.typography.titleMedium)

        if (reminders.isEmpty()) {
            // Message when no reminders saved
            Text("No upcoming reminders yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            // List of reminders displayed in cards
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(reminders) { reminder ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(reminder.title, style = MaterialTheme.typography.titleMedium)
                            Text("Date: ${reminder.date}", style = MaterialTheme.typography.bodyMedium)
                            Text(reminder.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
