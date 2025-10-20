package ie.setu.carmaintenenceapp.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import ie.setu.carmaintenenceapp.ui.viewmodel.ServiceReminder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

val Context.dataStore by preferencesDataStore("car_prefs")

object CarPrefsKeys {
    val CAR_REG = stringPreferencesKey("car_reg")
    val CAR_MAKE = stringPreferencesKey("car_make")
    val CAR_MODEL = stringPreferencesKey("car_model")
    val CAR_YEAR = intPreferencesKey("car_year")
    val CAR_MILEAGE = intPreferencesKey("car_mileage")
    val SERVICE_INTERVAL = intPreferencesKey("service_interval")
    val ENGINE_TYPE = stringPreferencesKey("engine_type")
    val ENGINE_SIZE = stringPreferencesKey("engine_size")
    val LAST_SERVICE_DATE = stringPreferencesKey("last_service_date")
    val REMINDERS = stringPreferencesKey("reminders_json")
}

fun saveCarData(context: Context, viewModel: ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel) = runBlocking {
    val json = Json.encodeToString(viewModel.reminders.toList())
    context.dataStore.edit { prefs ->
        prefs[CarPrefsKeys.CAR_REG] = viewModel.carReg.value
        prefs[CarPrefsKeys.CAR_MAKE] = viewModel.carMake.value
        prefs[CarPrefsKeys.CAR_MODEL] = viewModel.carModel.value
        prefs[CarPrefsKeys.CAR_YEAR] = viewModel.carYear.intValue
        prefs[CarPrefsKeys.CAR_MILEAGE] = viewModel.carMileage.intValue
        prefs[CarPrefsKeys.SERVICE_INTERVAL] = viewModel.serviceInterval.intValue
        prefs[CarPrefsKeys.ENGINE_TYPE] = viewModel.engineType.value
        prefs[CarPrefsKeys.ENGINE_SIZE] = viewModel.engineSize.value
        prefs[CarPrefsKeys.LAST_SERVICE_DATE] = viewModel.lastServiceDate.value
        prefs[CarPrefsKeys.REMINDERS] = json
    }
}

fun loadCarData(context: Context, viewModel: ie.setu.carmaintenenceapp.ui.viewmodel.CarViewModel) = runBlocking {
    val prefs = context.dataStore.data.map { it }.first()

    viewModel.carReg.value = prefs[CarPrefsKeys.CAR_REG] ?: ""
    viewModel.carMake.value = prefs[CarPrefsKeys.CAR_MAKE] ?: ""
    viewModel.carModel.value = prefs[CarPrefsKeys.CAR_MODEL] ?: ""
    viewModel.carYear.intValue = prefs[CarPrefsKeys.CAR_YEAR] ?: 0
    viewModel.carMileage.intValue = prefs[CarPrefsKeys.CAR_MILEAGE] ?: 0
    viewModel.serviceInterval.intValue = prefs[CarPrefsKeys.SERVICE_INTERVAL] ?: 10000
    viewModel.engineType.value = prefs[CarPrefsKeys.ENGINE_TYPE] ?: ""
    viewModel.engineSize.value = prefs[CarPrefsKeys.ENGINE_SIZE] ?: ""
    viewModel.lastServiceDate.value = prefs[CarPrefsKeys.LAST_SERVICE_DATE] ?: ""

    val remindersJson = prefs[CarPrefsKeys.REMINDERS]
    if (!remindersJson.isNullOrEmpty()) {
        val list = Json.decodeFromString<List<ServiceReminder>>(remindersJson)
        viewModel.reminders.clear()
        viewModel.reminders.addAll(list)
    }
}
