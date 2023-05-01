package com.hallen.asistentedeprofesores.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hallen.asistentedeprofesores.domain.model.Message

@Entity(tableName = "chat_table")
data class MessageEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")        var id: Int = 0,
    @ColumnInfo(name = "message")   var message: String = "",
    @ColumnInfo(name = "sender")    var sender: String = "",
    @ColumnInfo(name = "time")      var time: String = "",
    @ColumnInfo(name = "send")      var send: Boolean = false,
    @ColumnInfo(name = "error")     var error: Boolean = false
    )

fun Message.toDatabase() = MessageEntity(
    message = message,
    sender  = sender,
    time    = time,
    send    = send,
    error   = error
)
