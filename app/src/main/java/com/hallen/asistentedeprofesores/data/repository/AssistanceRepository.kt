package com.hallen.asistentedeprofesores.data.repository

import com.hallen.asistentedeprofesores.data.database.dao.AssistanceDao
import com.hallen.asistentedeprofesores.domain.model.Assistance
import com.hallen.asistentedeprofesores.domain.model.Day
import com.hallen.asistentedeprofesores.domain.model.toDomain
import javax.inject.Inject

class AssistanceRepository @Inject constructor(
    private val assistanceDao: AssistanceDao
){
    suspend fun setAssistance(
        id_student: Int,
        date: String,
        present: Int,
        qualification: Int = 0
    ) {
        assistanceDao.setAssistance(
            id_student    = id_student,
            date          = date,
            present       = present,
            qualification = qualification)
    }

    suspend fun getAllAssistanceByGroup(groupId: Int, date: String): List<Assistance> {
        val response = assistanceDao.getAllAssistanceByGroup(groupId, date)
        return response.map { it.toDomain() }
    }

    suspend fun getAllDays(groupId: Int): List<Day> {
        return assistanceDao.getAllDays(groupId)
    }

    suspend fun deleteDay(date: String, groupId: Int) {
        assistanceDao.deleteDay(date, groupId)
    }

    suspend fun getTotalDays(id_group: Int): Int {
        return assistanceDao.getTotalDays(id_group)
    }

    suspend fun getAllAssistanceFromDatabase(id_group: Int): List<Assistance> {
        val response = assistanceDao.getAllDaysFromDatabase(id_group)
        return response.map { it.toDomain()}
    }

}