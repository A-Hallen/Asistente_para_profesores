package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.repository.EventRepository
import com.hallen.asistentedeprofesores.domain.model.Event
import javax.inject.Inject

class GetEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend fun loadAllEvents(): List<Event> {
        return repository.getAllEvents()
    }

    suspend fun loadAllEvents(fecha: String): List<Event>{
        return repository.getAllEvents(fecha)
    }
}
