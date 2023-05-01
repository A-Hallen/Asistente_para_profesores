package com.hallen.asistentedeprofesores.data.repository

import com.hallen.asistentedeprofesores.data.database.dao.NotaDao
import com.hallen.asistentedeprofesores.data.database.entities.NotaEntity
import com.hallen.asistentedeprofesores.domain.model.Nota
import com.hallen.asistentedeprofesores.domain.model.toDomain
import javax.inject.Inject

class NotaRepository @Inject constructor(
    private val notaDao: NotaDao
){
    suspend fun getAllNotasFromDataBase(): List<Nota>{
        val response = notaDao.getAllNotas()
        return response.map { it.toDomain() }
    }

    suspend fun insertNota(nota: NotaEntity) {
        notaDao.insert(nota)
    }

    suspend fun updateNota(nota: NotaEntity){
        notaDao.update(nota)
    }

    suspend fun deleteNota(id: Int) {
        notaDao.delete(id)
    }
}
