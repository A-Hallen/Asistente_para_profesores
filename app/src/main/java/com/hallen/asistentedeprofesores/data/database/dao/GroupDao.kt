package com.hallen.asistentedeprofesores.data.database.dao

import androidx.room.*
import com.hallen.asistentedeprofesores.data.database.entities.GroupEntity
import com.hallen.asistentedeprofesores.domain.model.Group

@Dao
interface GroupDao {
    @Query("SELECT * FROM group_table ORDER BY id DESC")
    suspend fun getAllGroups(): List<GroupEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewGroup(group: GroupEntity): Long

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Query("DELETE FROM group_table WHERE id = :id")
    suspend fun deleteGroup(id: Int)

    @Query("SELECT name FROM group_table WHERE id = :id")
    suspend fun getGroupName(id: Int): String
}