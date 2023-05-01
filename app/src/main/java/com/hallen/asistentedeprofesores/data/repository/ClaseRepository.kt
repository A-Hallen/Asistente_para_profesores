package com.hallen.asistentedeprofesores.data.repository

import com.hallen.asistentedeprofesores.data.database.dao.ClaseDao
import com.hallen.asistentedeprofesores.data.database.entities.ClaseEntity
import com.hallen.asistentedeprofesores.domain.model.Clase
import com.hallen.asistentedeprofesores.domain.model.toDomain
import javax.inject.Inject

class ClaseRepository @Inject constructor(
    private val claseDao: ClaseDao
) {
    suspend fun getAllClasesFromDataBase(): List<Clase>{
        val response = claseDao.getAllClases()
        return response.map { it.toDomain() }
    }

    suspend fun insertClases(clases: List<ClaseEntity>){
        claseDao.instertAll(clases)
    }

    suspend fun deleteClase(clase: Clase) {
        claseDao.deleteClase(clase.id)
    }
}