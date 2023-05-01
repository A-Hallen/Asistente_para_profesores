package com.hallen.asistentedeprofesores.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hallen.asistentedeprofesores.domain.model.Nota

@Entity(tableName = "nota_table")
data class NotaEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")        var id: Int = 0,
    @ColumnInfo(name = "index")     var index: Int = 0,
    @ColumnInfo(name = "title")     var title: String = "",
    @ColumnInfo(name = "content")   var content: String = ""
    )

fun Nota.toDataBase() = NotaEntity(
    id,
    index,
    title,
    content
)