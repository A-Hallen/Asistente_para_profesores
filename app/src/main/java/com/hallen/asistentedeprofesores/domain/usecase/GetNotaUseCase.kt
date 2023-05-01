package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.repository.NotaRepository
import com.hallen.asistentedeprofesores.domain.model.Nota
import javax.inject.Inject

class GetNotaUseCase @Inject constructor(
    private val repository: NotaRepository
){
    suspend operator fun invoke(): List<Nota> = repository.getAllNotasFromDataBase()
}
