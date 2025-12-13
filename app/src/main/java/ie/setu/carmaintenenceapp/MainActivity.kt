package ie.setu.carmaintenenceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import ie.setu.carmaintenenceapp.ui.screens.HomeScreen
import ie.setu.carmaintenenceapp.ui.screens.ReminderScreen
import ie.setu.carmaintenenceapp.ui.screens.SettingsScreen
import ie.setu.carmaintenenceapp.data.CarDataStore
import ie.setu.carmaintenenceapp.ui.screens.SignUpScreen
import ie.setu.carmaintenenceapp.ui.theme.CarMaintenanceAppTheme
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModel for managing and storing UI state
        val viewModel: CarViewModel by viewModels()

        // DataStore instance for local persistence
        val dataStore = CarDataStore(applicationContext)

        // Load any previously saved data when app opens
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.loadData.collect { (car, reminders) ->
                viewModel.loadFromDataStore(car, reminders)
            }
        }

        enableEdgeToEdge() // Enables full screen UI

        setContent {

            // Collect saved dark mode preference
            val darkMode by dataStore.darkModeEnabled.collectAsState(initial = false)

            val userName = "Dominik" //hardcode for now just for testing

            var showSplash by rememberSaveable { mutableStateOf(true) }
            var showSignUp by rememberSaveable { mutableStateOf(false) }


            // Apply app theme based on darkMode setting
            CarMaintenanceAppTheme(darkTheme = darkMode) {
                if (showSplash) {
                    ie.setu.carmaintenenceapp.ui.screens.SplashScreen(
                        userName = userName,
                        onTimeout = {
                            showSplash = false
                            showSignUp = true
                        }
                    )
                } else if (showSignUp) {
                SignUpScreen(
                    onSignUpClick = { email, password ->
                        // Handle sign-up logic
                        showSignUp = false
                    },
                    onLoginClick = {
                        // Navigate to login screen
                        showSignUp = false
                    }
                )
            } else {
                CarMaintenanceApp(viewModel = viewModel, dataStore = dataStore)
            }
        }
    }
}

@Composable
fun CarMaintenanceApp(viewModel: CarViewModel, dataStore: CarDataStore) {

    // Tracks the currently selected tab/screen
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // Bottom navigation bar setup
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

        // Screen content displayed below the nav bar
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                // Shows main dashboard info
                AppDestinations.HOME -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel
                )

                // Shows all service reminders
                AppDestinations.REMINDERS -> ReminderScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    dataStore = dataStore
                )

                // Car profile + theme settings
                AppDestinations.SETTINGS -> SettingsScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    dataStore = dataStore
                )
            }
        }
    }
}

// Defines the 3 navigation destinations in the bottom bar
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
    CarMaintenanceAppTheme {
        Greeting("Android")
    }
}}
