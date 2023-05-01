package com.hallen.asistentedeprofesores.domain.model

import com.hallen.asistentedeprofesores.data.database.entities.ClaseEntity

data class Clase(
    var id: Int = 0,
    var asignatura: String = "",
    var grupo: String      = "",
    var dia: String        = "",
    var local: String      = "",
    var horaInicio: String = "",
    var horaFin: String    = "",
    var color: String      = "#FFFFFF"
)

fun ClaseEntity.toDomain() = Clase(
    id,
    asignatura,
    grupo,
    dia,
    local,
    horaInicio,
    horaFin,
    color
)