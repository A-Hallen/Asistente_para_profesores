package com.hallen.asistentedeprofesores.data.repository

import com.hallen.asistentedeprofesores.data.database.dao.GroupDao
import com.hallen.asistentedeprofesores.data.database.entities.GroupEntity
import com.hallen.asistentedeprofesores.domain.model.Group
import com.hallen.asistentedeprofesores.domain.model.toDomain
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {
    suspend fun getAllGroups(): List<Group>{
        val response = groupDao.getAllGroups()
        return response.map { it.toDomain() }
    }

    suspend fun insertNewGroup(group: GroupEntity): Long{
        return groupDao.insertNewGroup(group)
    }

    suspend fun updateGroup(group: GroupEntity){
        groupDao.updateGroup(group)
    }

    suspend fun deleteGroup(id: Int) {
        groupDao.deleteGroup(id)
    }

    suspend fun getGroupName(id: Int): String {
        return groupDao.getGroupName(id)
    }
}