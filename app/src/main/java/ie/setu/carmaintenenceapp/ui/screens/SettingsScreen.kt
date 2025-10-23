package ie.setu.carmaintenenceapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.carmaintenenceapp.data.CarDataStore
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: CarViewModel,
    dataStore: CarDataStore
) {
    var carMake by remember { mutableStateOf(viewModel.carMake.value) }
    var carReg by remember { mutableStateOf(viewModel.carReg.value) }
    var carYear by remember { mutableStateOf(viewModel.carYear.intValue.toString()) }
    var engineType by remember { mutableStateOf(viewModel.engineType.value) }
    var carMileage by remember { mutableStateOf(viewModel.carMileage.intValue.toString()) }
    var carModel by remember { mutableStateOf(viewModel.carModel.value) }
    var serviceInterval by remember { mutableStateOf(viewModel.serviceInterval.intValue.toString()) }
    var engineSize by remember { mutableStateOf(viewModel.engineSize.value) }
    var lastServiceDate by remember { mutableStateOf(viewModel.lastServiceDate.value) }
    val darkModeFlow = dataStore.darkModeEnabled.collectAsState(initial = false)
    var darkModeEnabled by remember { mutableStateOf(darkModeFlow.value) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Car Profile Settings", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = carReg,
            onValueChange = { carReg = it },
            label = { Text("Registration Number") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = carMake,
            onValueChange = { carMake = it },
            label = { Text("Car Make") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = carModel,
            onValueChange = { carModel = it },
            label = { Text("Car Model") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = carYear,
            onValueChange = { newVal -> carYear = newVal.filter { it.isDigit() } },
            label = { Text("Year of Manufacture") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = carMileage,
            onValueChange = { newVal -> carMileage = newVal.filter { it.isDigit() } },
            label = { Text("Current Mileage (km)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = serviceInterval,
            onValueChange = { newVal -> serviceInterval = newVal.filter { it.isDigit() } },
            label = { Text("Preferred Service Interval (km)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = engineType,
            onValueChange = { engineType = it },
            label = { Text("Engine Type") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = engineSize,
            onValueChange = { newVal -> engineSize = newVal.filter { it.isDigit() } },
            label = { Text("Engine Size (cc)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = lastServiceDate,
            onValueChange = { lastServiceDate = it },
            label = { Text("Last Service Date (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = darkModeEnabled,
                onCheckedChange = { enabled ->
                    darkModeEnabled = enabled
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveDarkMode(enabled)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.secondary
                )
            )
        }

        Spacer(Modifier.height(20.dp))


        Button(
            onClick = {
                val mileageValue = carMileage.toIntOrNull() ?: 0
                val intervalValue = serviceInterval.toIntOrNull() ?: 10000
                val yearValue = carYear.toIntOrNull() ?: 0

                viewModel.updateCarProfile(
                    reg = carReg,
                    mileage = mileageValue,
                    make = carMake,
                    model = carModel,
                    year = yearValue,
                    interval = intervalValue,
                    engineType = engineType,
                    engineSize = engineSize,
                    lastServiceDate = lastServiceDate
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
