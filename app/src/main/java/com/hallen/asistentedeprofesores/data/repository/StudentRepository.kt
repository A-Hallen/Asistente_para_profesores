package com.hallen.asistentedeprofesores.data.repository

import com.hallen.asistentedeprofesores.data.database.dao.StudentDao
import com.hallen.asistentedeprofesores.data.database.entities.StudentEntity
import com.hallen.asistentedeprofesores.domain.model.Student
import com.hallen.asistentedeprofesores.domain.model.StudentRegisters
import com.hallen.asistentedeprofesores.domain.model.toDomain
import javax.inject.Inject

class StudentRepository @Inject constructor(
    private val studentDao: StudentDao
) {
    suspend fun insertStudent(student: StudentEntity) {
        studentDao.insert(student)
    }

    suspend fun updateStudent(student: StudentEntity){
        if (studentDao.getStudent(student.id) == null){
            studentDao.insert(student)
        } else studentDao.update(student)
    }

    suspend fun deleteStudent(id: Int){
        studentDao.delete(id)
    }

    suspend fun getStudentsByGroupId(groupId: Int): List<Student> {
        val response = studentDao.getStudents(groupId)
        return response.map { it.toDomain()}
    }

    suspend fun deleteStudentsByGroupId(groupId: Int) {
        studentDao.deleteStudentsByGroupId(groupId)
    }

    suspend fun getStudentRegisters(groupId: Int): List<StudentRegisters> {
        return studentDao.getStudentRegisters(groupId)
    }

}