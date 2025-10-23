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

val Context.dataStore by preferencesDataStore("car_prefs")

class CarDataStore(private val context: Context) {
    companion object {
        private val CAR_DATA = stringPreferencesKey("car_data")
        private val REMINDERS = stringPreferencesKey("reminders")

        private val DARK_MODE = booleanPreferencesKey("dark_mode")

    }

    suspend fun saveCarData(car: CarProfile, reminders: List<ServiceReminder>) {
        context.dataStore.edit { prefs ->
            prefs[CAR_DATA] = Json.encodeToString(car)
            prefs[REMINDERS] = Json.encodeToString(reminders)
        }
    }
    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = enabled
        }
    }
    val loadData: Flow<Pair<CarProfile?, List<ServiceReminder>>> =
        context.dataStore.data.map { prefs ->
            val car = prefs[CAR_DATA]?.let { Json.decodeFromString<CarProfile>(it) }
            val reminders = prefs[REMINDERS]?.let {
                Json.decodeFromString<List<ServiceReminder>>(it)
            } ?: emptyList()
            car to reminders
        }
    val darkModeEnabled: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[DARK_MODE] ?: false }
}
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
