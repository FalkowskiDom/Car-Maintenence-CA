package ie.setu.carmaintenenceapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel


@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: CarViewModel) {
    var carName by remember { mutableStateOf(viewModel.carName.value) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = carName,
            onValueChange = { carName = it },
            label = { Text("Car Name") }
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.updateCarName(carName) }) {
            Text("Save Changes")
        }
    }
}
