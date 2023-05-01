package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.database.entities.toDataBase
import com.hallen.asistentedeprofesores.data.repository.GroupRepository
import com.hallen.asistentedeprofesores.domain.model.Group
import javax.inject.Inject

class SetGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {

    suspend fun getAllGroups(): List<Group> = groupRepository.getAllGroups()

    suspend fun insertGroup(group: Group): Long{
        return groupRepository.insertNewGroup(group.toDataBase())
    }

    suspend fun updateGroup(group: Group){
        groupRepository.updateGroup(group.toDataBase())
    }

    suspend fun deleteGroup(id: Int){
        groupRepository.deleteGroup(id)
    }

    suspend fun getGroupName(id: Int): String {
        return groupRepository.getGroupName(id)
    }
}