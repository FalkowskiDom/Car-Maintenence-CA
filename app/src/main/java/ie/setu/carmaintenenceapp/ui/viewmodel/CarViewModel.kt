package ie.setu.carmaintenenceapp.ui.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ie.setu.carmaintenenceapp.data.CarProfile
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data class for storing individual reminders
@Serializable
data class ServiceReminder(
    val title: String,
    val userId:String,
    val date: String,
    val time: String,
    val description: String,
    val id: String = java.util.UUID.randomUUID().toString()
)

class CarViewModel : ViewModel() {

    // State for car profile fields shown in the UI
    var carReg = mutableStateOf("")
    var carMileage = mutableIntStateOf(0)
    var carMake = mutableStateOf("")
    var carModel = mutableStateOf("")
    var carYear = mutableIntStateOf(0)
    var serviceInterval = mutableIntStateOf(10000)
    var engineType = mutableStateOf("")
    var engineSize = mutableStateOf("")
    var lastServiceDate = mutableStateOf("")

    // Calculates next service mileage automatically
    val nextServiceMileage: Int
        get() = carMileage.intValue + serviceInterval.intValue

    // Calculates the next expected service date (6 months later)
    val nextServiceDate: String
        get() = try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val lastDate = LocalDate.parse(lastServiceDate.value, formatter)
            lastDate.plusMonths(6).format(formatter)
        } catch (e: Exception) {
            "Not Set"
        }

    // List of upcoming reminders stored in ViewModel
    var reminders = mutableStateListOf<ServiceReminder>()

    // Adds a new reminder to the list
    fun addReminder(title: String, date: String,time: String, description: String): ServiceReminder {
        val reminder = ServiceReminder(title = title, date = date, time = time, description = description, userId = String() )
        reminders.add(reminder)
        return reminder
    }

    // Removes a reminder when user presses delete
    fun removeReminder(reminder: ServiceReminder) {
        reminders.remove(reminder)
    }

    // Updates all car profile values from SettingsScreen form
    fun updateCarProfile(
        reg: String,
        mileage: Int,
        make: String,
        model: String,
        year: Int,
        interval: Int,
        engineType: String,
        engineSize: String,
        lastServiceDate: String
    ) {
        carReg.value = reg
        carMileage.intValue = mileage
        carMake.value = make
        carModel.value = model
        carYear.intValue = year
        serviceInterval.intValue = interval
        this.engineSize.value = engineSize
        this.engineType.value = engineType
        this.lastServiceDate.value = lastServiceDate
    }

    // Loads saved data back into ViewModel from DataStore
    fun loadFromDataStore(profile: CarProfile?, reminders: List<ServiceReminder>) {
        if (profile != null) {
            carMake.value = profile.make
            carModel.value = profile.model
            carReg.value = profile.reg
            carMileage.intValue = profile.mileage
            carYear.intValue = profile.year
            engineType.value = profile.engineType
            engineSize.value = profile.engineSize
            serviceInterval.intValue = profile.serviceInterval
            lastServiceDate.value = profile.lastServiceDate
        }

        // Clear and re-add reminders so Compose UI updates correctly
        this.reminders.clear()
        this.reminders.addAll(reminders)
    }

    // Returns current car profile to save via DataStore
    fun getCurrentProfile(): CarProfile = CarProfile(
        make = carMake.value,
        model = carModel.value,
        reg = carReg.value,
        mileage = carMileage.intValue,
        year = carYear.intValue,
        engineType = engineType.value,
        engineSize = engineSize.value,
        serviceInterval = serviceInterval.intValue,
        lastServiceDate = lastServiceDate.value
    )
}
