package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.database.entities.toDataBase
import com.hallen.asistentedeprofesores.data.repository.EventRepository
import com.hallen.asistentedeprofesores.domain.model.Event
import javax.inject.Inject

class SetEventUseCase @Inject constructor(
    private val repository: EventRepository
) {

    suspend fun insertEvent(event: Event){
        repository.insertEvent(event.toDataBase())
    }

    suspend fun insertEvents(events: List<Event>){
        repository.insertAllEvents(events.map { it.toDataBase() })
    }

    suspend fun updateEvent(event: Event){
        repository.updateEvent(event.toDataBase())
    }

    suspend fun deleteEvent(id: Int){
        repository.deleteEvent(id)
    }
}