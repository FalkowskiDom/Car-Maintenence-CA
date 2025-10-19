package ie.setu.carmaintenenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import ie.setu.carmaintenenceapp.ui.theme.CarMaintenenceAppTheme
import ie.setu.carmaintenenceapp.ui.screens.HomeScreen
import ie.setu.carmaintenenceapp.ui.screens.ReminderScreen
import ie.setu.carmaintenenceapp.ui.screens.SettingsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarMaintenenceAppTheme {
                CarMaintenanceApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun CarMaintenanceApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val carViewModel: CarViewModel = viewModel()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(it.icon, contentDescription = it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = carViewModel
                )
                AppDestinations.REMINDERS -> ReminderScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = carViewModel
                )
                AppDestinations.SETTINGS -> SettingsScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = carViewModel
                )
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    REMINDERS("Reminders", Icons.Default.Favorite),
    SETTINGS("Settings", Icons.Default.AccountBox),
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CarMaintenenceAppTheme {
        Greeting("Android")
    }
}