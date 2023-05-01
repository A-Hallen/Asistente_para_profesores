package com.hallen.asistentedeprofesores.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.asistentedeprofesores.domain.model.*
import com.hallen.asistentedeprofesores.domain.usecase.AssistanceUseCase
import com.hallen.asistentedeprofesores.domain.usecase.SetGroupUseCase
import com.hallen.asistentedeprofesores.domain.usecase.StudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val setGroupUseCase: SetGroupUseCase,
    private val studentUseCase: StudentUseCase,
    private val assistanceUseCase: AssistanceUseCase
): ViewModel() {
    private val newDate = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date()) ?: ""

    var date               = MutableLiveData<String>()
    val studentDataModel   = MutableLiveData<List<StudentData>>()
    val groupModel         = MutableLiveData<List<Group>>()
    val groupNameModel     = MutableLiveData<String>()
    val groupRegisterModel = MutableLiveData<List<StudentRegisters>>()
    val daysInDatabase     = MutableLiveData<List<Day>>()
    var totalDays          = MutableLiveData<Int>()

    var totalAssistanceModel    = MutableLiveData<List<Pair<Int, List<Assistance>>>>()

    fun deleteDate(date: String, groupId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            assistanceUseCase.deleteDay(date, groupId)
            this@GroupViewModel.date.postValue(newDate)
            val registers = studentUseCase.getStudentRegisters(groupId)
            val days = assistanceUseCase.getAllDays(groupId)
            groupRegisterModel.postValue(registers)
            daysInDatabase.postValue(days)
            internalGetStudentData(groupId, newDate)
        }
    }

    private suspend fun getAllAssistanceFromDatabase(id_group: Int){
        val response = assistanceUseCase.getAllAssistanceFromDatabase(id_group)
        val groupResponse = response.groupBy { it.id_student }.toList()

        totalAssistanceModel.postValue(groupResponse)
    }

    private suspend fun getTotalDays(id_group: Int){
        val total = assistanceUseCase.getTotalDays(id_group)
        totalDays.postValue(total)
    }

    private suspend fun getStudentRegisters(groupId: Int){
        getTotalDays(groupId)
        val registers = studentUseCase.getStudentRegisters(groupId)
        groupRegisterModel.postValue(registers)
    }

    fun getDays(groupId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val days = assistanceUseCase.getAllDays(groupId)
            daysInDatabase.postValue(days)
        }
    }

    fun getStudentData(groupId: Int, date: String){
        CoroutineScope(Dispatchers.IO).launch {
            studentDataModel.postValue(emptyList())
            internalGetStudentData(groupId, date)
        }
    }

    private suspend fun internalGetStudentData(groupId: Int, date: String) {
        val students = studentUseCase.getStudentsByGroupId(groupId)
        val asistencia = assistanceUseCase.getAllAssistanceByGroup(groupId, date)
        val studentsData = arrayListOf<StudentData>()
        for (student in students){
            studentsData.add(
                StudentData(
                    id       = student.id,
                    name     = student.name,
                    id_group = student.id_group,
                    asistencia = asistencia.firstOrNull { it.id_student == student.id }
                )
            )
        }
        studentDataModel.postValue(studentsData)
        val groupName = setGroupUseCase.getGroupName(groupId)
        groupNameModel.postValue(groupName)
        getStudentRegisters(groupId)
        getAllAssistanceFromDatabase(groupId)
    }

    fun setAssistance(id_student: Int, date: String, present: Int , qualification: Int = 0){
        CoroutineScope(Dispatchers.IO).launch {
            assistanceUseCase.setAssistance(
                id_student = id_student,
                date = date,
                present = present,
                qualification = qualification
            )
        }
    }

    fun getGroups(){
        CoroutineScope(Dispatchers.IO).launch {
            val groups = setGroupUseCase.getAllGroups()
            groupModel.postValue(groups)
        }
    }

    fun saveGroup(group: List<Student>, name: String){
        val filterGroup = group.filter { it.name.isNotBlank() }
        CoroutineScope(Dispatchers.IO).launch {
            val groupId = setGroupUseCase.insertGroup(Group(name = name))
            val groups = setGroupUseCase.getAllGroups()
            groupModel.postValue(groups)
            for(student in filterGroup){
                student.id_group = groupId.toInt()
                studentUseCase.insertStudent(student)
            }
        }
    }

    fun updateGroup(group: List<Student>, name: String, id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            setGroupUseCase.updateGroup(Group(id = id, name = name))
            studentUseCase.deleteStudentsByGroupId(id)
            for(student in group){
                student.id_group = id
                if (student.name.isBlank()){
                    studentUseCase.deleteStudent(student.id)
                } else studentUseCase.updateStudent(student)
            }
            internalGetStudentData(id, date.value ?: "")
            groupModel.postValue(setGroupUseCase.getAllGroups())
        }
    }

    fun deleteGroup(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            setGroupUseCase.deleteGroup(id)
            val groups = setGroupUseCase.getAllGroups()
            groupModel.postValue(groups)
        }
    }
}