package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.database.entities.toDataBase
import com.hallen.asistentedeprofesores.data.repository.ClaseRepository
import com.hallen.asistentedeprofesores.domain.model.Clase
import javax.inject.Inject

class SetClaseUseCase @Inject constructor(
    private val repository: ClaseRepository
) {
    suspend fun insertClases(claseList: List<Clase>){
        repository.insertClases(claseList.map { it.toDataBase() })
    }

    suspend fun deleteClase(clase: Clase){
        repository.deleteClase(clase)
    }
}
