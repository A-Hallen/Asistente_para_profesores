package com.hallen.asistentedeprofesores.data.repository

import com.hallen.asistentedeprofesores.data.database.dao.ChatDao
import com.hallen.asistentedeprofesores.data.database.entities.toDatabase
import com.hallen.asistentedeprofesores.domain.model.Message
import com.hallen.asistentedeprofesores.domain.model.toDomain
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {
    suspend fun insertMessage(message: Message) = chatDao.insertMessage(message.toDatabase())

    suspend fun getAllMessage(): List<Message> = chatDao.getAllMessage().map { it.toDomain() }

    suspend fun deleteConversation() = chatDao.deleteConversation()

    suspend fun delete(message: Message) {
        chatDao.delete(message.time)
    }
    suspend fun update(message: Message) = chatDao.update(message.toDatabase())
}