package com.hallen.asistentedeprofesores.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hallen.asistentedeprofesores.domain.model.Event

@Entity(tableName = "event_table")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")         var id: Int = 0,
    @ColumnInfo(name = "time")       var time: String = "",
    @ColumnInfo(name = "hora")       var hora: String = "",
    @ColumnInfo(name = "title")      var title: String = "",
    @ColumnInfo(name = "details")    var details: String = "",
    @ColumnInfo(name = "priority")   var priority: String = "",
    @ColumnInfo(name = "notificar")  var notificar: Boolean = false

)

fun Event.toDataBase() = EventEntity(
    id,
    time,
    hora,
    title,
    details,
    priority,
    notificar
)