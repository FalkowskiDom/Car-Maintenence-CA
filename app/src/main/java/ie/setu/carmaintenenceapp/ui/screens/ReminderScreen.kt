package ie.setu.carmaintenenceapp.ui.screens

import android.R.attr.label
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel

@Composable
fun ReminderScreen(modifier: Modifier = Modifier, viewModel: CarViewModel) {
    var openDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Service Reminders", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))

        Button(onClick = { openDialog = true }) {
            Text("Add Reminder")
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn {
            items(viewModel.reminders) { reminder ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(reminder.title, style = MaterialTheme.typography.titleMedium)
                        Text("Date: ${reminder.date}")
                        Text(reminder.description)
                    }
                }
            }
        }
    }

    if (openDialog) {
        AddReminderDialog(onDismiss = { openDialog = false }) { title, date, desc ->
            viewModel.addReminder(title, date, desc)
            openDialog = false
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit)
{
    val serviceTypes = listOf(
        "Oil Change",
        "Tire Rotation",
        "Brake Inspection",
        "Battery replacement",
        "Annual Service",
        "Engine Tune-up",
        "Coolant / Fluids",
        "NCT Appointment"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf(serviceTypes.first()) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var desc by remember { mutableStateOf("") }

    val dateText = remember(selectedDateMillis) {
        selectedDateMillis?.let { millis ->
            java.time.Instant.ofEpochMilli(millis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .toString()
        } ?: ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Service Reminder") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Service type dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        readOnly = true,
                        value = selectedService,
                        onValueChange = {},
                        label = { Text("Service Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        serviceTypes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedService = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    value = dateText,
                    onValueChange = {},
                    label = { Text("Service Date") },
                    placeholder = { Text("Select a date") },
                    supportingText = {
                        if (dateText.isEmpty()) Text("Select a date")
                    }
                )





            }
        },
        confirmButton = {
            Button(
                enabled = selectedDateMillis != null && selectedService.isNotEmpty(),
                onClick = {
                    val date = dateText
                    onAdd(selectedService, date, desc)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}