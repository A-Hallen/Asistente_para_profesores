package com.hallen.asistentedeprofesores.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.hallen.asistentedeprofesores.domain.model.Assistance

@Entity(tableName = "assistance_table", foreignKeys = [ForeignKey(entity = StudentEntity::class,parentColumns = ["id"],childColumns = ["id_student"],onDelete = CASCADE)])
data class AssistanceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")            val id: Int = 0,
    @ColumnInfo(name = "id_student")    var id_student: Int = 0,
    @ColumnInfo(name = "date")          var date: String = "",
    @ColumnInfo(name = "assistance")    var assistance: Int = 0,
    @ColumnInfo(name = "qualification") var qualification: Int = 0
)

fun Assistance.toDataBase() = AssistanceEntity(
    id,
    id_student,
    date,
    assistance,
    qualification
)