package com.hallen.asistentedeprofesores.domain.model

import com.hallen.asistentedeprofesores.data.database.entities.EventEntity


data class Event(
    var id: Int = 0,
    var time: String,
    var hora: String,
    var title: String = "",
    var details: String = "",
    var priority: String,
    var notificar: Boolean
)

fun EventEntity.toDomain() = Event(
    id,
    time,
    hora,
    title,
    details,
    priority,
    notificar
)
