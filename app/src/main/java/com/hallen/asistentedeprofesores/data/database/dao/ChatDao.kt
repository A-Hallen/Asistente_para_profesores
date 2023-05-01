package com.hallen.asistentedeprofesores.data.database.dao

import androidx.room.*
import com.hallen.asistentedeprofesores.data.database.entities.MessageEntity
import com.hallen.asistentedeprofesores.domain.model.Message

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM chat_table ORDER BY time ASC")
    suspend fun getAllMessage(): List<MessageEntity>

    @Query("DELETE FROM chat_table")
    suspend fun deleteConversation()

    @Query("DELETE FROM chat_table WHERE time = :time")
    suspend fun delete(time: String)

    @Update(entity = MessageEntity::class)
    suspend fun update(message: MessageEntity)

}