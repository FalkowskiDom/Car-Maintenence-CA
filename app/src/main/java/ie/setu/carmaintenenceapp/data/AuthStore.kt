package ie.setu.carmaintenenceapp.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class UserAccount(
    val email: String,
    val password: String
)

@Serializable
data class UsersFile(
    val users: List<UserAccount> = emptyList()
)

class AuthStore(private val context: Context) {

    private val file = File(context.filesDir, "users.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private suspend fun readUsers(): UsersFile = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext UsersFile()
        val text = file.readText()
        if (text.isBlank()) UsersFile()
        else json.decodeFromString(text)
    }

    private suspend fun writeUsers(usersFile: UsersFile) = withContext(Dispatchers.IO) {
        file.writeText(json.encodeToString(usersFile))
    }

    suspend fun signUp(emailRaw: String, password: String): Result<Unit> {
        val email = emailRaw.trim().lowercase()

        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password required"))
        }

        val current = readUsers()

        if (current.users.any { it.email == email }) {
            return Result.failure(IllegalStateException("User already exists"))
        }

        val updated = current.copy(
            users = current.users + UserAccount(email, password)
        )

        writeUsers(updated)
        return Result.success(Unit)
    }

    suspend fun login(emailRaw: String, password: String): Result<Unit> {
        val email = emailRaw.trim().lowercase()

        val current = readUsers()
        val user = current.users.firstOrNull { it.email == email }

        return if (user != null && user.password == password) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        }
    }
}
