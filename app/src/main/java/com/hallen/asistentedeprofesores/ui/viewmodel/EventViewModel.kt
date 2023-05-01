package com.hallen.asistentedeprofesores.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.asistentedeprofesores.domain.usecase.GetEventUseCase
import com.hallen.asistentedeprofesores.domain.usecase.SetEventUseCase
import com.hallen.asistentedeprofesores.domain.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventUseCase: GetEventUseCase,
    private val setEventUseCase: SetEventUseCase
): ViewModel(){

    private var actualDate: String = ""

    val eventModel = MutableLiveData<List<Event>>()
    val dayEvents  = MutableLiveData<List<Event>>()

    fun getEvents(){
        CoroutineScope(Dispatchers.IO).launch {
            val events = getEventUseCase.loadAllEvents()
            eventModel.postValue(events)
        }
    }

    fun getEvents(actualDate: String) {
        this.actualDate = actualDate
        CoroutineScope(Dispatchers.IO).launch {
            val events = getEventUseCase.loadAllEvents(actualDate)
            dayEvents.postValue(events)
        }
    }

    fun saveEvent(event: Event){
        CoroutineScope(Dispatchers.IO).launch {
            setEventUseCase.insertEvent(event)
            getEvents()
            getEvents(actualDate)
        }
    }

    fun deleteEvent(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            setEventUseCase.deleteEvent(id)
            getEvents()
            getEvents(actualDate)
        }
    }

}