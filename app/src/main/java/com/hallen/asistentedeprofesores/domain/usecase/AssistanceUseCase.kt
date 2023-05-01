package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.repository.AssistanceRepository
import com.hallen.asistentedeprofesores.domain.model.Assistance
import com.hallen.asistentedeprofesores.domain.model.Day
import javax.inject.Inject

class AssistanceUseCase @Inject constructor(
    private val repository: AssistanceRepository
) {
    suspend fun setAssistance(
        id_student: Int,
        date: String,
        present: Int,
        qualification: Int = 0
    ) {
        repository.setAssistance(
            id_student    = id_student,
            date          = date,
            present       = present,
            qualification = qualification)
    }
    suspend fun getAllAssistanceByGroup(groupId: Int, date: String): List<Assistance> {
        return repository.getAllAssistanceByGroup(groupId, date)
    }

    suspend fun getAllDays(groupId: Int): List<Day> {
        return repository.getAllDays(groupId)
    }

    suspend fun deleteDay(date: String, groupId: Int) {
        repository.deleteDay(date, groupId)
    }

    suspend fun getTotalDays(id_group: Int): Int {
        return repository.getTotalDays(id_group)
    }

    suspend fun getAllAssistanceFromDatabase(id_group: Int): List<Assistance> {
        return repository.getAllAssistanceFromDatabase(id_group)
    }
}