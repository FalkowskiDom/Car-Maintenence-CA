package ie.setu.carmaintenenceapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ie.setu.carmaintenenceapp.ui.viewmodel.ServiceReminder
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

// Creates a single DataStore instance for the whole app (persistent storage)
val Context.dataStore by preferencesDataStore("car_prefs")

class CarDataStore(private val context: Context) {

    companion object {
        // Keys used to save and retrieve JSON data from DataStore
        private val CAR_DATA = stringPreferencesKey("car_data")
        private val REMINDERS = stringPreferencesKey("reminders")

        // Key for storing dark mode setting
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    //Save everything to DataStore (car profile + reminders list)
    suspend fun saveCarData(car: CarProfile, reminders: List<ServiceReminder>) {
        context.dataStore.edit { prefs ->
            // Convert objects into JSON strings before saving
            prefs[CAR_DATA] = Json.encodeToString(car)
            prefs[REMINDERS] = Json.encodeToString(reminders)
        }
    }

    //Store the dark mode toggle setting
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = enabled
        }
    }

    //Flow that continuously returns saved values when changed
    val loadData: Flow<Pair<CarProfile?, List<ServiceReminder>>> =
        context.dataStore.data.map { prefs ->
            // Load car profile from JSON (if saved)
            val car = prefs[CAR_DATA]?.let { Json.decodeFromString<CarProfile>(it) }

            // Load reminders list from JSON
            val reminders = prefs[REMINDERS]?.let {
                Json.decodeFromString<List<ServiceReminder>>(it)
            } ?: emptyList()

            // Return pair of data so ViewModel can update UI
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