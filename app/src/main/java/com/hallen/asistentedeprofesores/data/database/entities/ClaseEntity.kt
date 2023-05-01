package com.hallen.asistentedeprofesores.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hallen.asistentedeprofesores.domain.model.Clase

@Entity(tableName = "clase_table")
data class ClaseEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")        var id: Int = 0,
    @ColumnInfo(name=  "asignatura")var asignatura: String = "",
    @ColumnInfo(name = "grupo")     var grupo: String      = "",
    @ColumnInfo(name = "dia")       var dia: String        = "",
    @ColumnInfo(name = "local")     var local: String      = "",
    @ColumnInfo(name = "horaInicio")var horaInicio: String = "",
    @ColumnInfo(name = "horaFin")   var horaFin: String    = "",
    @ColumnInfo(name = "color")     var color: String      = "#FFFFFF"
)

fun Clase.toDataBase() = ClaseEntity(
    id,
    asignatura,
    grupo,
    dia,
    local,
    horaInicio,
    horaFin,
    color
)