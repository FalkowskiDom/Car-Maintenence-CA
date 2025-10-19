package ie.setu.carmaintenenceapp.ui.screens

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

@Composable
fun AddReminderDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Service Reminder") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(title, date, desc) }) {
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
