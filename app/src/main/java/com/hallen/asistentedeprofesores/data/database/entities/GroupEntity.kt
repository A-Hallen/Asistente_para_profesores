package com.hallen.asistentedeprofesores.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hallen.asistentedeprofesores.domain.model.Group

@Entity(tableName = "group_table")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")   var id: Int = 0,
    @ColumnInfo(name = "name") var name: String = ""
)

fun Group.toDataBase() = GroupEntity(
    id,
    name
)
