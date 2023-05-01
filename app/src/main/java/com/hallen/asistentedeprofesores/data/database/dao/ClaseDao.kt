package com.hallen.asistentedeprofesores.data.database.dao

import androidx.room.*
import com.hallen.asistentedeprofesores.data.database.entities.ClaseEntity
import com.hallen.asistentedeprofesores.domain.model.Clase

@Dao
interface ClaseDao {
    @Query("SELECT * FROM clase_table ORDER BY horaInicio DESC")
    suspend fun getAllClases(): List<ClaseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun instertAll(clases: List<ClaseEntity>)

    @Query("DELETE FROM clase_table WHERE id = :id")
    suspend fun deleteClase(id: Int)
}