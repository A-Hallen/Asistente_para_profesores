package com.hallen.asistentedeprofesores.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.hallen.asistentedeprofesores.domain.model.Student

@Entity(tableName = "students_table", foreignKeys = [
    ForeignKey(
        entity = GroupEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_group"],
        onDelete = CASCADE
        )
])
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")        var id: Int = 0,
    @ColumnInfo(name = "name")      var name: String = "",
    @ColumnInfo(name = "id_group")  var id_group: Int = 0
)

fun Student.toDataBase() = StudentEntity(
    id,
    name,
    id_group
)
