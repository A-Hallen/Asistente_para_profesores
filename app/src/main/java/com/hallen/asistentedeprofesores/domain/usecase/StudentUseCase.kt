package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.database.entities.toDataBase
import com.hallen.asistentedeprofesores.data.repository.StudentRepository
import com.hallen.asistentedeprofesores.domain.model.Student
import com.hallen.asistentedeprofesores.domain.model.StudentRegisters
import javax.inject.Inject

class StudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {

    suspend fun getStudentsByGroupId(groupId: Int): List<Student>{
        return repository.getStudentsByGroupId(groupId)
    }

    suspend fun insertStudent(student: Student){
        repository.insertStudent(student.toDataBase())
    }

    suspend fun updateStudent(student: Student){
        repository.updateStudent(student.toDataBase())
    }

    suspend fun deleteStudent(id: Int){
        repository.deleteStudent(id)
    }

    suspend fun deleteStudentsByGroupId(groupId: Int) {
        repository.deleteStudentsByGroupId(groupId)
    }

    suspend fun getStudentRegisters(groupId: Int): List<StudentRegisters> {
        return repository.getStudentRegisters(groupId)
    }
}