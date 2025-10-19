package ie.setu.carmaintenenceapp.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class ServiceReminder(
    val title: String,
    val date: String,
    val description: String
)

class CarViewModel : ViewModel() {
    var carName = mutableStateOf("BMW 520D")
    var reminders = mutableStateListOf<ServiceReminder>()

    fun addReminder(title: String, date: String, description: String) {
        reminders.add(ServiceReminder(title, date, description))
    }

    fun updateCarName(newName: String) {
        carName.value = newName
    }
}
