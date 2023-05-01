package com.hallen.asistentedeprofesores.domain.model

import com.hallen.asistentedeprofesores.data.database.entities.StudentEntity

data class Student(
    var id: Int = 0,
    var name: String = "",
    var id_group: Int = 0
)

fun StudentEntity.toDomain() = Student(
    id,
    name,
    id_group
)