package com.hallen.asistentedeprofesores.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hallen.asistentedeprofesores.data.database.dao.*
import com.hallen.asistentedeprofesores.data.database.entities.*


@Database(entities = [
        AssistanceEntity::class,
        ClaseEntity::class,
        EventEntity::class,
        GroupEntity::class,
        NotaEntity::class,
        StudentEntity::class,
        MessageEntity::class
                     ], version = 1
)
abstract class DataBase: RoomDatabase() {


    abstract fun getAssistanceDao(): AssistanceDao
    abstract fun getClaseDao(): ClaseDao
    abstract fun getEventDao(): EventDao
    abstract fun getGroupDao(): GroupDao
    abstract fun getNotaDao(): NotaDao
    abstract fun getStudentDao(): StudentDao
    abstract fun getChatDao(): ChatDao
}