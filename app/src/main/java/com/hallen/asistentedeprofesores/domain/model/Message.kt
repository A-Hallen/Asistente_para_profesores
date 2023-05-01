package com.hallen.asistentedeprofesores.domain.model

import com.hallen.asistentedeprofesores.data.database.entities.MessageEntity

data class Message(
    var id: Int = 0,
    var message: String = "",
    var sender: String = "",
    var time: String = "",
    var send: Boolean = false,
    var error: Boolean = false
)

fun MessageEntity.toDomain() = Message(
    id,
    message,
    sender,
    time,
    send,
    error
)