package com.hallen.asistentedeprofesores.data.database.dao

import androidx.room.*
import com.hallen.asistentedeprofesores.data.database.entities.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM event_table ORDER BY id DESC")
    suspend fun getAllEvents(): List<EventEntity>

    @Query("SELECT * FROM event_table WHERE time = :fecha")
    suspend fun getAllEvents(fecha: String): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Update
    suspend fun update(event: EventEntity)

    @Query("DELETE FROM event_table WHERE id = :id")
    suspend fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)
}