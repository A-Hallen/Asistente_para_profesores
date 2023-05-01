package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.database.entities.toDataBase
import com.hallen.asistentedeprofesores.data.repository.NotaRepository
import com.hallen.asistentedeprofesores.domain.model.Nota
import javax.inject.Inject

class SetNotaUseCase @Inject constructor(
    private val repository: NotaRepository
){

    suspend fun insertNota(nota: Nota) {
        repository.insertNota(nota.toDataBase())
    }

    suspend fun updateNota(nota: Nota){
        repository.updateNota(nota.toDataBase())
    }

    suspend fun deleteNota(id: Int) {
        repository.deleteNota(id)
    }
}
