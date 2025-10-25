package ie.setu.carmaintenenceapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ie.setu.carmaintenenceapp.data.CarDataStore
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: CarViewModel, dataStore: CarDataStore) {

    val coroutineScope = rememberCoroutineScope()

    // Dark mode toggle state
    val darkMode by dataStore.darkModeEnabled.collectAsState(initial = false)

    // Controls whether the bottom sheet edit form is visible
    var showEditSheet by remember { mutableStateOf(false) }

    // Current saved car profile
    val car = viewModel.getCurrentProfile()

    // Layout that contains the screen content and bottom bar for settings
    Scaffold(
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)

                // UI switch to enable/disable dark theme
                Switch(
                    checked = darkMode,
                    onCheckedChange = { coroutineScope.launch { dataStore.setDarkMode(it) } },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.titleLarge)

            // Card showing current car details
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("${car.make} ${car.model}", style = MaterialTheme.typography.titleMedium)
                    Text("Reg: ${car.reg}")
                    Text("Mileage: ${car.mileage} km")
                    Text("Engine: ${car.engineType} ${car.engineSize}")
                    Text("Next Service: every ${car.serviceInterval} km")
                    Text("Last Serviced: ${car.lastServiceDate}")

                    Spacer(Modifier.height(12.dp))

                    // Opens the editable sheet for car changes
                    Button(
                        onClick = { showEditSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Edit Car Profile") }
                }
            }
        }
    }

    // Shows bottom sheet only when editing
    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            EditCarForm(
                viewModel = viewModel,
                dataStore = dataStore,
                onClose = { showEditSheet = false }
            )
        }
    }
}

@Composable
fun EditCarForm(viewModel: CarViewModel, dataStore: CarDataStore, onClose: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()

    // Temporary state holders for user edits
    var make by remember { mutableStateOf(viewModel.carMake.value) }
    var model by remember { mutableStateOf(viewModel.carModel.value) }
    var reg by remember { mutableStateOf(viewModel.carReg.value) }
    var mileage by remember { mutableStateOf(viewModel.carMileage.intValue.toString()) }
    var year by remember { mutableStateOf(viewModel.carYear.intValue.toString()) }
    var engineType by remember { mutableStateOf(viewModel.engineType.value) }
    var engineSize by remember { mutableStateOf(viewModel.engineSize.value) }
    var serviceInterval by remember { mutableStateOf(viewModel.serviceInterval.intValue.toString()) }
    var lastServiceDate by remember { mutableStateOf(viewModel.lastServiceDate.value) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Current calendar date for initializing picker
    val yearNow = calendar.get(Calendar.YEAR)
    val monthNow = calendar.get(Calendar.MONTH)
    val dayNow = calendar.get(Calendar.DAY_OF_MONTH)

    // Opens native Android date picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            lastServiceDate = "$dayOfMonth/${month + 1}/$year"
        },
        yearNow, monthNow, dayNow
    )

    // Form list in scrollable container
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Edit Car Profile", style = MaterialTheme.typography.titleMedium)
        }

        item {
            // Inputs for each stored property
            OutlinedTextField(make, { make = it }, label = { Text("Make") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(model, { model = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(reg, { reg = it }, label = { Text("Registration") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                mileage,
                { mileage = it },
                label = { Text("Mileage") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                year,
                { year = it },
                label = { Text("Year") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(engineType, { engineType = it }, label = { Text("Engine Type") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(engineSize, { engineSize = it }, label = { Text("Engine Size") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                serviceInterval,
                { serviceInterval = it },
                label = { Text("Service Interval (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Triggers the date picker
            OutlinedTextField(
                value = lastServiceDate,
                onValueChange = {},
                label = { Text("Last Service Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(16.dp))

            // Save changes and persist to DataStore
            Button(
                onClick = {
                    viewModel.updateCarProfile(
                        reg = reg,
                        mileage = mileage.toIntOrNull() ?: 0,
                        make = make,
                        model = model,
                        year = year.toIntOrNull() ?: 2010,
                        interval = serviceInterval.toIntOrNull() ?: 10000,
                        engineType = engineType,
                        engineSize = engineSize,
                        lastServiceDate = lastServiceDate
                    )

                    coroutineScope.launch {
                        dataStore.saveCarData(viewModel.getCurrentProfile(), viewModel.reminders)
                    }

                    onClose()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
