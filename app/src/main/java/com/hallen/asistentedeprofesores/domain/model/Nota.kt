package com.hallen.asistentedeprofesores.domain.model

import com.hallen.asistentedeprofesores.data.database.entities.NotaEntity

data class Nota(
    var id: Int = 0,
    var index: Int = 0,
    var title: String = "",
    var content: String = ""
)

fun NotaEntity.toDomain() = Nota(
    id,
    index,
    title,
    content
)