package ie.setu.carmaintenenceapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ie.setu.carmaintenenceapp.ui.viewmodel.ServiceReminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Creates a single DataStore instance for the whole app (persistent storage)
val Context.dataStore by preferencesDataStore("car_prefs")

class CarDataStore(private val context: Context) {

    companion object {
        // Keys used to save and retrieve JSON data from DataStore

        // Key for storing dark mode setting
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    // Per-user keys
    private fun carKey(userId: String) = stringPreferencesKey("car_data_$userId")
    private fun remindersKey(userId: String) = stringPreferencesKey("reminders_$userId")

    // Save everything to DataStore (car profile + reminders list)
    suspend fun saveCarData(userId: String, car: CarProfile, reminders: List<ServiceReminder>) {
        context.dataStore.edit { prefs ->
            prefs[carKey(userId)] = Json.encodeToString(car)
            prefs[remindersKey(userId)] = Json.encodeToString(reminders)
        }
    }

    //Store the dark mode toggle setting
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = enabled
        }
    }

    //Flow that continuously returns saved values when changed
    fun loadData(userId: String): Flow<Pair<CarProfile?, List<ServiceReminder>>> =
        context.dataStore.data.map { prefs ->
            val car = prefs[carKey(userId)]?.let { Json.decodeFromString<CarProfile>(it) }

            val reminders = prefs[remindersKey(userId)]?.let {
                Json.decodeFromString<List<ServiceReminder>>(it)
            } ?: emptyList()

            car to reminders
        }
    //Returns true/false to set theme mode
    val darkModeEnabled: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[DARK_MODE] ?: false }
}

//Holds all car information that must be saved
@Serializable
data class CarProfile(
    val make: String,
    val model: String,
    val reg: String,
    val mileage: Int,
    val year: Int,
    val engineType: String,
    val engineSize: String,
    val serviceInterval: Int,
    val lastServiceDate: String
)