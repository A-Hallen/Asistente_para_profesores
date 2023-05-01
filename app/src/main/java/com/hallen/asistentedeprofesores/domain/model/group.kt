package com.hallen.asistentedeprofesores.domain.model

import com.hallen.asistentedeprofesores.data.database.entities.GroupEntity

data class Group(
    var id: Int = 0,
    var name: String = ""
)

fun GroupEntity.toDomain() = Group(
    id,
    name
)