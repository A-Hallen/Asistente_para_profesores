package com.hallen.asistentedeprofesores.data.database.dao

import androidx.room.*
import com.hallen.asistentedeprofesores.data.database.entities.NotaEntity

@Dao
interface NotaDao {

    @Query("SELECT * FROM nota_table ORDER BY id DESC")
    suspend fun getAllNotas(): List<NotaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notas: List<NotaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nota: NotaEntity)

    @Update
    suspend fun update(nota: NotaEntity)

    @Query("DELETE FROM nota_table WHERE id = :id")
    suspend fun delete(id: Int)
}
