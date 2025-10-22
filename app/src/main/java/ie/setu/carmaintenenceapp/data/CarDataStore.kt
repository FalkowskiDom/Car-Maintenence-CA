package ie.setu.carmaintenenceapp.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("car_prefs")

class CarDataStore(private val context: Context) {
    companion object {
    private val CAR_REG = stringPreferencesKey("car_reg")
    private val CAR_MAKE = stringPreferencesKey("car_make")
    private val CAR_MODEL = stringPreferencesKey("car_model")
    private val CAR_YEAR = intPreferencesKey("car_year")
    private val CAR_MILEAGE = intPreferencesKey("car_mileage")
    private val SERVICE_INTERVAL = intPreferencesKey("service_interval")
    private val ENGINE_TYPE = stringPreferencesKey("engine_type")
    private val ENGINE_SIZE = stringPreferencesKey("engine_size")
    private val LAST_SERVICE_DATE = stringPreferencesKey("last_service_date")
    private val REMINDERS = stringPreferencesKey("reminders_json")
}

    suspend fun saveCarData(
        make: String,
        model: String,
        reg: String,
        mileage: Int,
        year: Int,
        engineType: String,
        engineSize: String,
        serviceInterval: Int,
        lastServiceDate: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[CAR_MAKE] = make
            prefs[CAR_MODEL] = model
            prefs[CAR_REG] = reg
            prefs[CAR_MILEAGE] = mileage
            prefs[CAR_YEAR] = year
            prefs[ENGINE_TYPE] = engineType
            prefs[ENGINE_SIZE] = engineSize
            prefs[SERVICE_INTERVAL] = serviceInterval
            prefs[LAST_SERVICE_DATE] = lastServiceDate
        }
    }
    val carDataFlow = context.dataStore.data.map { prefs ->
        CarProfile(
            make = prefs[CAR_MAKE] ?: "",
            model = prefs[CAR_MODEL] ?: "",
            reg = prefs[CAR_REG] ?: "",
            mileage = prefs[CAR_MILEAGE] ?: 0,
            year = prefs[CAR_YEAR] ?: 0,
            engineType = prefs[ENGINE_TYPE] ?: "",
            engineSize = prefs[ENGINE_SIZE] ?: "",
            serviceInterval = prefs[SERVICE_INTERVAL] ?: 10000,
            lastServiceDate = prefs[LAST_SERVICE_DATE] ?: ""
        )
    }
}

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
