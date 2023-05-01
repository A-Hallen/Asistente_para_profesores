package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.repository.ClaseRepository
import com.hallen.asistentedeprofesores.domain.model.Clase
import javax.inject.Inject

class GetClaseUseCase @Inject constructor(
    private val repository: ClaseRepository
) {
    suspend operator fun invoke(): List<Clase> = repository.getAllClasesFromDataBase()
}