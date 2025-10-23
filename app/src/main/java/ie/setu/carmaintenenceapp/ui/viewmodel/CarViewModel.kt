package ie.setu.carmaintenenceapp.ui.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ie.setu.carmaintenenceapp.data.CarProfile
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
@Serializable
data class ServiceReminder(
    val title: String,
    val date: String,
    val description: String
)
class CarViewModel : ViewModel() {
    var carReg = mutableStateOf("")
    var carMileage = mutableIntStateOf(0)
    var carMake = mutableStateOf("")
    var carModel = mutableStateOf("")
    var carYear = mutableIntStateOf(0)
    var serviceInterval = mutableIntStateOf(10000)
    var engineType = mutableStateOf("")
    var engineSize = mutableStateOf("")
    var lastServiceDate = mutableStateOf("")

    val nextServiceMileage: Int
    get() = carMileage.intValue + serviceInterval.intValue

    val nextServiceDate: String
        get() = try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val lastDate = LocalDate.parse(lastServiceDate.value, formatter)
            lastDate.plusMonths(6).format(formatter)
        } catch (e: Exception) {
            "Not Set"
        }
    var reminders = mutableStateListOf<ServiceReminder>()
    fun addReminder(title: String, date: String, description: String) {
        reminders.add(ServiceReminder(title, date, description))
    }

    fun removeReminder(reminder: ServiceReminder) {
        reminders.remove(reminder)
    }
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
        this.reminders.clear()
        this.reminders.addAll(reminders)
    }

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