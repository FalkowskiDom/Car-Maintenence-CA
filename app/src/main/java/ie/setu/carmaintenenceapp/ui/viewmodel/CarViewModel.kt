package ie.setu.carmaintenenceapp.ui.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
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
        engineSize: String
    ) {
        carReg.value = reg
        carMileage.intValue = mileage
        carMake.value = make
        carModel.value = model
        carYear.intValue = year
        serviceInterval.intValue = interval
        this.engineSize.value = engineSize
        this.engineType.value = engineType
    }
}