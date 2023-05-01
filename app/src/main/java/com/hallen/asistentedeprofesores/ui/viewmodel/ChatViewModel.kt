package com.hallen.asistentedeprofesores.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.asistentedeprofesores.domain.model.Message
import com.hallen.asistentedeprofesores.domain.usecase.ChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase
): ViewModel(){
    val messageModel = MutableLiveData<List<Message>>()

    fun getMessages(){
        CoroutineScope(Dispatchers.IO).launch {
            val messages = chatUseCase.getAllMessage()
            messageModel.postValue(messages)
        }
    }

    fun insertMessage(message: Message){
        CoroutineScope(Dispatchers.IO).launch {
            chatUseCase.insertMessage(message)
            val messages = chatUseCase.getAllMessage()
            messageModel.postValue(messages)
        }
    }

    fun deleteMessage(message: Message){
        CoroutineScope(Dispatchers.IO).launch {
            chatUseCase.delete(message)
        }
    }

    fun deleteConversation(){
        CoroutineScope(Dispatchers.IO).launch {
            chatUseCase.deleteConversation()
            messageModel.postValue(emptyList())
        }
    }

    fun update(message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            chatUseCase.update(message)
        }
    }
}