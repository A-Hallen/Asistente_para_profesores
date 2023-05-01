package com.hallen.asistentedeprofesores.domain.model

import com.hallen.asistentedeprofesores.data.database.entities.AssistanceEntity

data class Assistance(
    var id: Int = 0,
    var id_student: Int = 0,
    var date: String = "",
    var assistance: Int = 0,
    var qualification: Int = 0
)

fun AssistanceEntity.toDomain() = Assistance(
    id,
    id_student,
    date,
    assistance,
    qualification
)