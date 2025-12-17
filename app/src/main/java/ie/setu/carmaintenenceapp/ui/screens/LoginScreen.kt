package ie.setu.carmaintenenceapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ie.setu.carmaintenenceapp.data.AuthStore
import ie.setu.carmaintenenceapp.data.CarDataStore
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authStore: AuthStore,
    dataStore: CarDataStore,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
//    onBypassClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val darkMode by dataStore.darkModeEnabled.collectAsState(initial = false)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = !loading,
            onClick = {
                loading = true
                scope.launch {
                    val result = authStore.login(email, password)
                    loading = false

                    result.onSuccess { onLoginSuccess() }
                        .onFailure { error = it.message ?: "Login failed" }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        //this part is for testing purposes
//        OutlinedButton(
//            onClick = onBypassClick,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Bypass")
//        }

        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign up")
        }
    }
}}
