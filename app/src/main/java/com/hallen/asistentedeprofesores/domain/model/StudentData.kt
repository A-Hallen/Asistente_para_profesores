package com.hallen.asistentedeprofesores.domain.model

class StudentData(
    var id: Int = 0,
    var name: String = "",
    var id_group: Int = 0,
    var asistencia: Assistance? = null
)