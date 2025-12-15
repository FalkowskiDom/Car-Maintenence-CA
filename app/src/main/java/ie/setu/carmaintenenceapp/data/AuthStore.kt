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
    val password: String,
    val username: String
)
@Serializable
data class SessionFile(
    val email: String,
    val username: String
)
@Serializable
data class UsersFile(
    val users: List<UserAccount> = emptyList()
)

class AuthStore(private val context: Context) {

    private val usersFile = File(context.filesDir, "users.json")
    private val sessionFile = File(context.filesDir, "session.json")


    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private suspend fun readUsers(): UsersFile = withContext(Dispatchers.IO) {
        if (!usersFile.exists()) return@withContext UsersFile()

        val text = usersFile.readText()
        if (text.isBlank()) return@withContext UsersFile()

        runCatching { json.decodeFromString<UsersFile>(text) }
            .getOrElse { UsersFile() }
    }

    private suspend fun writeSession(session: SessionFile) = withContext(Dispatchers.IO) {
        sessionFile.writeText(json.encodeToString(session))
    }
    private suspend fun writeUsers(users: UsersFile) = withContext(Dispatchers.IO) {
        usersFile.writeText(json.encodeToString(users))
    }

    suspend fun getSession(): SessionFile? = withContext(Dispatchers.IO) {
        if (!sessionFile.exists()) return@withContext null

        val text = sessionFile.readText()
        if (text.isBlank()) return@withContext null

        runCatching { json.decodeFromString<SessionFile>(text) }
            .getOrNull()
    }

    suspend fun clearSession() = withContext(Dispatchers.IO) {
        if (sessionFile.exists()) sessionFile.delete()
    }

    suspend fun signUp(emailRaw: String, password: String, usernameRaw: String): Result<Unit> {
        val email = emailRaw.trim().lowercase()
        val username = usernameRaw.trim()

        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password required"))
        }

        val current = readUsers()

        if (current.users.any { it.email == email }) {
            return Result.failure(IllegalStateException("User already exists"))
        }

        val updated = current.copy(
            users = current.users + UserAccount(email, password, username)
        )

        writeUsers(updated)
        writeSession(SessionFile(email = email, username = username))
        return Result.success(Unit)
    }

    suspend fun login(emailRaw: String, password: String): Result<Unit> {
        val email = emailRaw.trim().lowercase()

        val current = readUsers()
        val user = current.users.firstOrNull { it.email == email }

        return if (user != null && user.password == password) {
            writeSession(SessionFile(email = user.email, username = user.username))
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        }
    }
}
