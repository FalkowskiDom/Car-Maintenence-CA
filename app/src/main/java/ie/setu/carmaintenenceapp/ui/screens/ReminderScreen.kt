package ie.setu.carmaintenenceapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.carmaintenenceapp.data.CarDataStore
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel
import androidx.compose.ui.platform.LocalContext
import ie.setu.carmaintenenceapp.notifications.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId


// Helper extension to create a clickable area without ripple animation
@Composable
private fun Modifier.noIndicationClickable(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    )


@Composable
fun ReminderScreen(
    modifier: Modifier = Modifier,
    viewModel: CarViewModel,
    dataStore: CarDataStore
) {
    // Controls showing the Add Reminder dialog
    var openDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var reminderToDelete by remember { mutableStateOf<ie.setu.carmaintenenceapp.ui.viewmodel.ServiceReminder?>(null) }
    val context = LocalContext.current


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Service Reminders", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))

        // Button to show add reminder dialog
        Button(
            onClick = { openDialog = true },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text("Add Reminder")
        }

        Spacer(Modifier.height(12.dp))

        // List of saved reminders from ViewModel
        LazyColumn {
            items(viewModel.reminders) { reminder ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(reminder.title, style = MaterialTheme.typography.titleMedium)
                            Text("Date: ${reminder.date}")
                            Text(reminder.description)
                        }

                        // Delete reminder button
                        IconButton(onClick = {
                            reminderToDelete = reminder

                            // Persist change to DataStore
                            coroutineScope.launch(Dispatchers.IO) {
                                dataStore.saveCarData(
                                    viewModel.getCurrentProfile(),
                                    viewModel.reminders
                                )
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Delete reminder")
                        }
                    }
                }
            }
        }
        if (reminderToDelete != null) {
            AlertDialog(
                onDismissRequest = { reminderToDelete = null },
                title = { Text("Delete reminder?") },
                text = { Text("Are you sure you want to delete this reminder?") },
                confirmButton = {
                    Button(onClick = {
                        val toDelete = reminderToDelete!!
                        reminderToDelete = null

                        viewModel.removeReminder(toDelete)

                        // Persist change to DataStore
                        coroutineScope.launch(Dispatchers.IO) {
                            dataStore.saveCarData(
                                viewModel.getCurrentProfile(),
                                viewModel.reminders
                            )
                        }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { reminderToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

    }

    // Add Reminder Dialog
    if (openDialog) {
        AddReminderDialog(onDismiss = { openDialog = false }) { title, date, desc ->
            val created = viewModel.addReminder(title, date, desc)
            ReminderScheduler.schedule(context, created)


            // Save updated data to persistence
            coroutineScope.launch(Dispatchers.IO) {
                dataStore.saveCarData(viewModel.getCurrentProfile(), viewModel.reminders)
            }

            openDialog = false
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    // Dropdown list of reminder types
    val serviceTypes = listOf(
        "Oil Change", "Tire Rotation", "Brake Inspection",
        "Battery Replacement", "Annual Service", "Engine Tune-up",
        "Coolant / Fluids", "NCT Appointment"
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf(serviceTypes.first()) }

    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    var desc by remember { mutableStateOf("") }

    // Convert stored milliseconds to LocalDate to show in UI
    val dateText = remember(selectedDateMillis) {
        selectedDateMillis?.let { millis ->
            Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString()
        } ?: ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Service Reminder") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Service Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selectedService,
                        onValueChange = {},
                        label = { Text("Service Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
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

                // Date picker display field
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    value = dateText,
                    onValueChange = {},
                    label = { Text("Service Date") },
                    placeholder = { Text("Select a date") },
                )

                // Overlay to trigger actual DatePicker dialog
                Box(
                    modifier = Modifier
                        .offset(y = (-76).dp)
                        .height(56.dp)
                        .fillMaxWidth()
                        .noIndicationClickable { showDatePicker = true }
                )

                // Description text field
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                )
            }
        },

        // Confirm and save reminder
        confirmButton = {
            Button(
                enabled = selectedDateMillis != null && selectedService.isNotEmpty(),
                onClick = {
                    onAdd(selectedService, dateText, desc)
                }
            ) { Text("Add") }
        },

        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

    // Material3 DatePicker dialog
    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = pickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}