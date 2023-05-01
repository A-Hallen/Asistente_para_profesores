package com.hallen.asistentedeprofesores.domain.usecase

import com.hallen.asistentedeprofesores.data.repository.ChatRepository
import com.hallen.asistentedeprofesores.domain.model.Message
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend fun insertMessage(message: Message) = repository.insertMessage(message)

    suspend fun getAllMessage(): List<Message> = repository.getAllMessage()

    suspend fun deleteConversation() = repository.deleteConversation()

    suspend fun delete(message: Message) = repository.delete(message)

    suspend fun update(message: Message) = repository.update(message)
}