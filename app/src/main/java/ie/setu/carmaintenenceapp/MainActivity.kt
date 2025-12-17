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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import ie.setu.carmaintenenceapp.data.AuthStore
import ie.setu.carmaintenenceapp.data.CarDataStore
import ie.setu.carmaintenenceapp.ui.screens.HomeScreen
import ie.setu.carmaintenenceapp.ui.screens.LoginScreen
import ie.setu.carmaintenenceapp.ui.screens.ReminderScreen
import ie.setu.carmaintenenceapp.ui.screens.SettingsScreen
import ie.setu.carmaintenenceapp.ui.screens.SignUpScreen
import ie.setu.carmaintenenceapp.ui.screens.SplashScreen
import ie.setu.carmaintenenceapp.ui.theme.CarMaintenanceAppTheme
import ie.setu.carmaintenenceapp.notifications.NotificationHelper
import ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private val requestNotifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // ViewModel for managing and storing UI state
        val viewModel: CarViewModel by viewModels()

        val authStore = AuthStore(applicationContext)

        // DataStore instance for local persistence
        val dataStore = CarDataStore(applicationContext)
        NotificationHelper.createChannel(this)

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

            var loadingSession by remember { mutableStateOf(true) }
            var sessionUserName by rememberSaveable { mutableStateOf("") }

            var showSplash by rememberSaveable { mutableStateOf(true) }
            var inApp by rememberSaveable { mutableStateOf(false) }
            var authIsLogin by rememberSaveable { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                val session = authStore.getSession()
                if (session != null) {
                    sessionUserName = session.username
                    inApp = true // user stays logged in
                }
                loadingSession = false
            }
            val userName = sessionUserName.ifBlank { "" }
            // Apply app theme based on darkMode setting
            CarMaintenanceAppTheme(darkTheme = darkMode) {
                if (loadingSession){
                    Text("Loading...")
                    return@CarMaintenanceAppTheme
                }
                // The 'when' statement handles the navigation flow: Splash -> Auth -> Main App
                when {
                    showSplash -> {
                        SplashScreen(
                            userName = userName,
                            onTimeout = {
                                showSplash = false
                                authIsLogin = true // Default to login screen after splash
                            }
                        )
                    }
                    inApp -> {
                        // Once authenticated, show the main app content
                        CarMaintenanceApp(
                            viewModel = viewModel,
                            dataStore = dataStore,
                            authStore = authStore,
                            onLoggedOut = {
                                inApp = false
                                authIsLogin = true
                                sessionUserName = ""
                                showSplash = false
                            },
                            userName = userName
                        )
                    }
                    authIsLogin -> {
                        // Show the login screen
                        LoginScreen(
                            authStore = authStore,
                            dataStore = dataStore,
                            onLoginSuccess = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    sessionUserName = authStore.getSession()?.username.orEmpty()
                                    inApp = true } },
                            onSignUpClick = { authIsLogin = false }, // Switch to sign-up screen
                            onBypassClick = { inApp = true } // For testing
                        )
                    }
                    else -> {
                        // Show the sign-up screen
                        SignUpScreen(
                            authStore = authStore,
                            dataStore = dataStore,
                            onSignUpSuccess = { CoroutineScope(Dispatchers.Main).launch {
                                sessionUserName = authStore.getSession()?.username.orEmpty()
                                inApp = true }},
                            onLoginClick = { authIsLogin = true } // Switch back to login screen
                        )
                    }
                }
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
fun CarMaintenanceApp(
    viewModel: CarViewModel,
    dataStore: CarDataStore,
    authStore: AuthStore,
    onLoggedOut: () -> Unit,
    userName: String
) {

    // Tracks the currently selected tab/screen
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // Bottom navigation bar setup
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        // Screen content displayed based on the selected destination
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                // Shows main dashboard info
                AppDestinations.HOME -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    userName = userName
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
                    dataStore = dataStore,
                    authStore = authStore,
                    onLoggedOut = onLoggedOut,
                    userName = userName
                )
            }
        }
    }
}
